package daniking.birdsnests;

import com.google.common.collect.ImmutableList;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BirdsNests implements ModInitializer {

    public static final String MODID = "birdsnests";
    public static final Logger LOGGER = LogManager.getLogger(BirdsNests.class);
    private static final List<Identifier> LOOT_TABLE_IDENTIFIERS = ImmutableList.of(Blocks.OAK_LEAVES.getLootTableId(),
            Blocks.SPRUCE_LEAVES.getLootTableId(),
            Blocks.BIRCH_LEAVES.getLootTableId(),
            Blocks.JUNGLE_LEAVES.getLootTableId(),
            Blocks.ACACIA_LEAVES.getLootTableId(),
            Blocks.DARK_OAK_LEAVES.getLootTableId());
    public static ConfigFile configFile;
    public static Item nest;

    @Override
    public void onInitialize() {
        AutoConfig.register(ConfigFile.class, GsonConfigSerializer::new);
        configFile = AutoConfig.getConfigHolder(ConfigFile.class).getConfig();
        // Done for late static initialization
        nest = new NestItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(configFile.maxCount));
        Registry.register(Registry.ITEM, new Identifier(MODID, "nest"), nest);
        registerLootTable(Blocks.OAK_LEAVES.getLootTableId());
        registerLootTable(Blocks.SPRUCE_LEAVES.getLootTableId());
        registerLootTable(Blocks.BIRCH_LEAVES.getLootTableId());
        registerLootTable(Blocks.JUNGLE_LEAVES.getLootTableId());
        registerLootTable(Blocks.ACACIA_LEAVES.getLootTableId());
        registerLootTable(Blocks.DARK_OAK_LEAVES.getLootTableId());
        LOGGER.info("BirdsNests Initialized");
    }

    private static void registerLootTable(Identifier lootTableId) {
        LOOT_TABLE_IDENTIFIERS.forEach(identifier -> {
            if (identifier.equals(lootTableId)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(ConstantLootTableRange.create(1))
                        .withCondition(RandomChanceLootCondition.builder((float) configFile.nestDropChance).build())
                        .withEntry(ItemEntry.builder(nest).build());

                Registry.BLOCK.forEach(block -> {
                    if (block.getLootTableId().equals(lootTableId)) {
                        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
                            if (id.equals(lootTableId)) {
                                supplier.withPool(poolBuilder.build());
                            }
                        });
                    }
                });
            }
        });
    }
}
