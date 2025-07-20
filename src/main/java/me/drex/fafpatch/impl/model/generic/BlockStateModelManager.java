package me.drex.fafpatch.impl.model.generic;

import com.faboslav.friendsandfoes.common.FriendsAndFoes;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.BlockStateAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateModelVariant;
import eu.pb4.polymer.resourcepack.extras.api.format.blockstate.StateMultiPartDefinition;
import me.drex.fafpatch.impl.FriendsAndFoesPatch;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;

public class BlockStateModelManager {
    private static final Map<BlockState, List<ModelGetter>> MAP = new HashMap<>();
    private static final Map<BlockState, ParticleOptions> PARTICLE = new HashMap<>();
    public static final Map<ResourceLocation, List<StateModelVariant>> UV_LOCKED_MODELS = new HashMap<>();

    public static List<ModelGetter> get(BlockState state) {
        return MAP.getOrDefault(state, List.of());
    }
    public static ParticleOptions getParticle(BlockState state) {
        return PARTICLE.getOrDefault(state, ParticleTypes.ANGRY_VILLAGER);
    }

    public static void addBlock(ResourceLocation identifier, Block block) {
        try {
            var rand = RandomSource.create(123);
            var path = FabricLoader.getInstance().getModContainer(FriendsAndFoes.MOD_ID).get()
                    .findPath("assets/" + identifier.getNamespace() + "/blockstates/" + identifier.getPath() + ".json").get();

            var decoded = BlockStateAsset.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(path)));
            var modelDef = decoded.getOrThrow().getFirst();

            if (modelDef.variants().isPresent()) {
                var list = new ArrayList<Tuple<BlockStatePredicate, List<ModelData>>>();
                parseVariants(block, modelDef.variants().get(), list);

                for (var pair : list) {
                    for (var state : block.getStateDefinition().getPossibleStates()) {
                        if (pair.getA().test(state)) {
                            MAP.put(state, List.of(ModelGetter.of(pair.getB())));
                            if (!pair.getB().isEmpty()) {
                                PARTICLE.put(state, new ItemParticleOption(ParticleTypes.ITEM, pair.getB().getFirst().stack));
                            }
                        }
                    }
                }
            }

            if (modelDef.multipart().isPresent()) {
                var list = new ArrayList<Tuple<List<BlockStatePredicate>, List<ModelData>>>();
                parseMultipart(block, modelDef.multipart().get(), list);

                for (var pair : list) {
                    for (var state : block.getStateDefinition().getPossibleStates()) {
                        for (var pred : pair.getA()) {
                            if (pred.test(state)) {
                                var objects = new ArrayList<ModelGetter>();
                                if (MAP.containsKey(state)) {
                                    objects.addAll(MAP.get(state));
                                }
                                objects.add(ModelGetter.of(pair.getB()));
                                MAP.put(state, objects);
                                if (!objects.isEmpty() && !PARTICLE.containsKey(state)) {
                                    PARTICLE.put(state, new ItemParticleOption(ParticleTypes.ITEM, objects.getFirst().getModel(rand).stack));
                                }

                                break;
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            FriendsAndFoesPatch.LOGGER.warn("Failed to decode model for {}", identifier, e);
        }
    }

    private static void parseMultipart(Block block, List<StateMultiPartDefinition> multiPartDefinition, ArrayList<Tuple<List<BlockStatePredicate>, List<ModelData>>> list) {
        for (var part : multiPartDefinition) {
            var preds = new ArrayList<BlockStatePredicate>();

            if (part.when().or().isPresent()) {
                for (var x : part.when().or().get()) {
                    var predicate = BlockStatePredicate.forBlock(block);
                    applyWhenMultipart(predicate, block, x);
                    preds.add(predicate);
                }
            }

            if (part.when().and().isPresent()) {
                var predicate = BlockStatePredicate.forBlock(block);
                for (var x : part.when().or().get()) {
                    applyWhenMultipart(predicate, block, x);
                }
                preds.add(predicate);
            }

            if (part.when().base().isPresent()) {
                var predicate = BlockStatePredicate.forBlock(block);
                applyWhenMultipart(predicate, block, part.when().base().get());
                preds.add(predicate);
            }

            if (preds.isEmpty()) {
                preds.add(BlockStatePredicate.forBlock(block));
            }

            var modelData = parseBaseVariants(part.apply());
            list.add(new Tuple<>(preds, modelData));
        }
    }

    private static void applyWhenMultipart(BlockStatePredicate predicate, Block block, Map<String, String> x) {
        for (var entry : x.entrySet()) {
            //noinspection rawtypes
            var prop = (Property) block.getStateDefinition().getProperty(entry.getKey());

            if (prop == null) {
                continue;
            }

            var split = Set.of(entry.getValue().split("\\|"));

            //noinspection rawtypes,unchecked
            predicate.where(prop, y -> split.contains(prop.getName((Comparable) y)));
        }
    }


    private static void parseVariants(Block block, Map<String, List<StateModelVariant>> modelDef, ArrayList<Tuple<BlockStatePredicate, List<ModelData>>> list) {
        parseVariants(block, modelDef, (a, b) -> {
            var modelData = parseBaseVariants(b);
            list.add(new Tuple<>(a, modelData));
        });
    }
    public static void parseVariants(Block block, Map<String, List<StateModelVariant>> modelDef, BiConsumer<BlockStatePredicate, List<StateModelVariant>> consumer) {
        start:
        for (var pair : modelDef.entrySet()) {
            var stateMap = pair.getKey().split(",");

            var predicate = BlockStatePredicate.forBlock(block);


            for (var statePair : stateMap) {
                if (!statePair.isEmpty()) {
                    var split = statePair.split("=", 2);
                    var prop = (Property) block.getStateDefinition().getProperty(split[0]);

                    if (prop == null) {
                        continue start;
                    }

                    predicate.where(prop, x -> prop.getName((Comparable) x).equals(split[1]));
                }
            }

            consumer.accept(predicate, pair.getValue());
        }
    }

    private static List<ModelData> parseBaseVariants(List<StateModelVariant> value) {
        var modelData = new ArrayList<ModelData>();

        for (var v : value) {
            if (v.uvlock()) {
                var modelId = v.model().withSuffix("_uvlock_" + v.x() + "_" + v.y());

                var stack = ItemDisplayElementUtil.getModel(modelId);
                modelData.add(new ModelData(stack, new Quaternionf()
                        .rotateY(-Mth.DEG_TO_RAD * v.y())
                        .rotateX(Mth.DEG_TO_RAD * v.x()),
                        v.weigth()
                ));

                UV_LOCKED_MODELS.computeIfAbsent(v.model(), x -> new ArrayList<>()).add(v);
            } else {
                var stack = ItemDisplayElementUtil.getModel(v.model());
                modelData.add(new ModelData(stack, new Quaternionf()
                        .rotateY(-Mth.DEG_TO_RAD * v.y())
                        .rotateX(Mth.DEG_TO_RAD * v.x()),
                        v.weigth()
                ));
            }
        }

        return modelData;
    }

    public interface ModelGetter {
        ModelData getModel(RandomSource random);

        static ModelGetter of(List<ModelData> data) {
            if (data.size() == 1) {
                return new SingleGetter(data.get(0));
            }

            return WeightedGetter.create(data);
        }
    }


    private record SingleGetter(ModelData data) implements ModelGetter {
        @Override
        public ModelData getModel(RandomSource random) {
            return this.data;
        }
    }

    private record WeightedGetter(WeightedList<ModelData> weightedList) implements ModelGetter {
        public static ModelGetter create(List<ModelData> data) {
            var list = new ArrayList<Weighted<ModelData>>();
            for (var d : data) {
                list.add(new Weighted<>(d, d.weight));
            }
            return new WeightedGetter(WeightedList.of(list));
        }

        @Override
        public ModelData getModel(RandomSource random) {
            return weightedList.getRandomOrThrow(random);
        }
    }
    public record ModelData(ItemStack stack, Quaternionfc quaternionfc, int weight) {}
}
