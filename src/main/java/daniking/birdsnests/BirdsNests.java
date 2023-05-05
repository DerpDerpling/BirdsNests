package daniking.birdsnests;

import com.google.common.collect.ImmutableList;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class BirdsNests implements ModInitializer {

    public static final String MODID = "birdsnests";
    public static final Logger LOGGER = LoggerFactory.getLogger(BirdsNests.class);
    private static final List<Identifier> LOOT_TABLE_IDENTIFIERS = ImmutableList.of(
            Blocks.OAK_LEAVES.getLootTableId(),
            Blocks.SPRUCE_LEAVES.getLootTableId(),
            Blocks.BIRCH_LEAVES.getLootTableId(),
            Blocks.JUNGLE_LEAVES.getLootTableId(),
            Blocks.ACACIA_LEAVES.getLootTableId(),
            Blocks.DARK_OAK_LEAVES.getLootTableId(),
            Blocks.CHERRY_LEAVES.getLootTableId()
    );
    public static ConfigFile configFile;
    public static Item nest;

    @Override
    public void onInitialize() {
        AutoConfig.register(ConfigFile.class, GsonConfigSerializer::new);
        configFile = AutoConfig.getConfigHolder(ConfigFile.class).getConfig();
        // Done for late static initialization
        nest = new NestItem(new FabricItemSettings().maxCount(configFile.maxCount));
        Registry.register(Registries.ITEM, new Identifier(MODID, "nest"), nest);
        registerLootTables();
        LOGGER.info("BirdsNests Initialized");
    }

    static void registerLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin()) {
                for (final Identifier entry : LOOT_TABLE_IDENTIFIERS) {
                    if (id.equals(entry)) {
                        tableBuilder.pool(buildLoot().build());
                        break;
                    }
                }
            }
        });
    }

    static LootPool.Builder buildLoot() {
        return LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1))
                .conditionally(RandomChanceLootCondition.builder((float) configFile.nestDropChance).build())
                .with(ItemEntry.builder(nest).build());
    }
}
