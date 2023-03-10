package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.APCapabilities;
import gg.archipelago.aprandomizer.capability.data.WorldData;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static gg.archipelago.aprandomizer.APRandomizer.*;

public class LocationManager {

    private final HashMap<String, Integer> advancementData = new HashMap<>() {{
        put("minecraft:nether/obtain_crying_obsidian", 42000);
        put("minecraft:nether/distract_piglin", 42001);
        put("minecraft:story/obtain_armor", 42002);
        put("minecraft:adventure/very_very_frightening", 42003);
        put("minecraft:story/lava_bucket", 42004);
        put("minecraft:end/kill_dragon", 42005);
        put("minecraft:nether/all_potions", 42006);
        put("minecraft:husbandry/tame_an_animal", 42007);
        put("minecraft:nether/create_beacon", 42008);
        put("minecraft:story/deflect_arrow", 42009);
        put("minecraft:story/iron_tools", 42010);
        put("minecraft:nether/brew_potion", 42011);
        put("minecraft:end/dragon_egg", 42012);
        put("minecraft:husbandry/fishy_business", 42013);
        put("minecraft:nether/explore_nether", 42014);
        put("minecraft:nether/ride_strider", 42015);
        put("minecraft:adventure/sniper_duel", 42016);
        put("minecraft:nether/root", 42017);
        put("minecraft:end/levitate", 42018);
        put("minecraft:nether/all_effects", 42019);
        put("minecraft:adventure/bullseye", 42020);
        put("minecraft:nether/get_wither_skull", 42021);
        put("minecraft:husbandry/bred_all_animals", 42022);
        put("minecraft:story/mine_stone", 42023);
        put("minecraft:adventure/two_birds_one_arrow", 42024);
        put("minecraft:story/enter_the_nether", 42025);
        put("minecraft:adventure/whos_the_pillager_now", 42026);
        put("minecraft:story/upgrade_tools", 42027);
        put("minecraft:husbandry/tactical_fishing", 42028);
        put("minecraft:story/cure_zombie_villager", 42029);
        put("minecraft:end/find_end_city", 42030);
        put("minecraft:story/form_obsidian", 42031);
        put("minecraft:end/enter_end_gateway", 42032);
        put("minecraft:nether/obtain_blaze_rod", 42033);
        put("minecraft:nether/loot_bastion", 42034);
        put("minecraft:adventure/shoot_arrow", 42035);
        put("minecraft:husbandry/silk_touch_nest", 42036);
        put("minecraft:adventure/arbalistic", 42037);
        put("minecraft:end/respawn_dragon", 42038);
        put("minecraft:story/smelt_iron", 42039);
        put("minecraft:nether/charge_respawn_anchor", 42040);
        put("minecraft:story/shiny_gear", 42041);
        put("minecraft:end/elytra", 42042);
        put("minecraft:adventure/summon_iron_golem", 42043);
        put("minecraft:nether/return_to_sender", 42044);
        put("minecraft:adventure/sleep_in_bed", 42045);
        put("minecraft:end/dragon_breath", 42046);
        put("minecraft:adventure/root", 42047);
        put("minecraft:adventure/kill_all_mobs", 42048);
        put("minecraft:story/enchant_item", 42049);
        put("minecraft:adventure/voluntary_exile", 42050);
        put("minecraft:story/follow_ender_eye", 42051);
        put("minecraft:end/root", 42052);
        put("minecraft:husbandry/obtain_netherite_hoe", 42053);
        put("minecraft:adventure/totem_of_undying", 42054);
        put("minecraft:adventure/kill_a_mob", 42055);
        put("minecraft:adventure/adventuring_time", 42056);
        put("minecraft:husbandry/plant_seed", 42057);
        put("minecraft:nether/find_bastion", 42058);
        put("minecraft:adventure/hero_of_the_village", 42059);
        put("minecraft:nether/obtain_ancient_debris", 42060);
        put("minecraft:nether/create_full_beacon", 42061);
        put("minecraft:nether/summon_wither", 42062);
        put("minecraft:husbandry/balanced_diet", 42063);
        put("minecraft:nether/fast_travel", 42064);
        put("minecraft:husbandry/root", 42065);
        put("minecraft:nether/use_lodestone", 42066);
        put("minecraft:husbandry/safely_harvest_honey", 42067);
        put("minecraft:adventure/trade", 42068);
        put("minecraft:nether/uneasy_alliance", 42069);
        put("minecraft:story/mine_diamond", 42070);
        put("minecraft:nether/find_fortress", 42071);
        put("minecraft:adventure/throw_trident", 42072);
        put("minecraft:story/root", 42073);
        put("minecraft:adventure/honey_block_slide", 42074);
        put("minecraft:adventure/ol_betsy", 42075);
        put("minecraft:nether/netherite_armor", 42076);
        put("minecraft:story/enter_the_end", 42077);
        put("minecraft:husbandry/breed_an_animal", 42078);
        put("minecraft:husbandry/complete_catalogue", 42079);
        put("aprandomizer:archipelago/get_wood", 42080);
        put("aprandomizer:archipelago/get_pickaxe", 42081);
        put("aprandomizer:archipelago/hot_topic", 42082);
        put("aprandomizer:archipelago/bake_bread", 42083);
        put("aprandomizer:archipelago/the_lie", 42084);
        put("aprandomizer:archipelago/ride_minecart", 42085);
        put("aprandomizer:archipelago/craft_sword", 42086);
        put("aprandomizer:archipelago/cow_tipper", 42087);
        put("aprandomizer:archipelago/ride_pig", 42088);
        put("aprandomizer:archipelago/overkill", 42089);
        put("aprandomizer:archipelago/obtain_bookshelf", 42090);
        put("aprandomizer:archipelago/overpowered", 42091);
        put("minecraft:husbandry/wax_on", 42092);
        put("minecraft:husbandry/wax_off", 42093);
        put("minecraft:husbandry/axolotl_in_a_bucket", 42094);
        put("minecraft:husbandry/kill_axolotl_target", 42095);
        put("minecraft:adventure/spyglass_at_parrot", 42096);
        put("minecraft:adventure/spyglass_at_ghast", 42097);
        put("minecraft:adventure/spyglass_at_dragon", 42098);
        put("minecraft:adventure/lightning_rod_with_villager_no_fire", 42099);
        put("minecraft:adventure/walk_on_powder_snow_with_leather_boots", 42100);
        put("minecraft:husbandry/make_a_sign_glow", 42101);
        put("minecraft:husbandry/ride_a_boat_with_a_goat", 42102);
        put("minecraft:adventure/fall_from_world_height", 42103);
        put("minecraft:nether/ride_strider_in_overworld_lava", 42104);
        put("minecraft:adventure/play_jukebox_in_meadows", 42105);
        put("minecraft:adventure/trade_at_world_height", 42106);
        // 1.19 advancements
        put("minecraft:husbandry/allay_deliver_cake_to_note_block", 42107);
        put("minecraft:husbandry/tadpole_in_a_bucket", 42108);
        put("minecraft:adventure/kill_mob_near_sculk_catalyst", 42109);
        put("minecraft:adventure/avoid_vibration", 42110);
        put("minecraft:husbandry/leash_all_frog_variants", 42111);
        put("minecraft:husbandry/froglights", 42112);
        put("minecraft:husbandry/allay_deliver_item_to_player", 42113);

    }};

    private final HashMap<String, Integer> layerData = new HashMap<>();

    public final Set<ResourceLocation> hardAdvancements = new HashSet<>() {{
        add(new ResourceLocation("adventure/very_very_frightening")); // Very Very Frightening
        add(new ResourceLocation("nether/all_potions")); // A Furious Cocktail
        add(new ResourceLocation("husbandry/bred_all_animals")); // Two by Two
        add(new ResourceLocation("adventure/two_birds_one_arrow")); // Two Birds, One Arrow
        add(new ResourceLocation("adventure/arbalistic")); // Arbalistic
        add(new ResourceLocation("adventure/kill_all_mobs")); // Monsters Hunted
        add(new ResourceLocation("nether/create_full_beacon")); // Beaconator
        add(new ResourceLocation("husbandry/balanced_diet")); // A Balanced Diet
        add(new ResourceLocation("nether/uneasy_alliance")); // Uneasy Alliance
        add(new ResourceLocation("nether/netherite_armor")); // Cover Me in Debris
        add(new ResourceLocation("husbandry/complete_catalogue")); // A Complete Catalogue
        add(new ResourceLocation("adventure/lightning_rod_with_villager_no_fire")); // Surge Protector
        add(new ResourceLocation("adventure/play_jukebox_in_meadows")); // Sound of Music
        add(new ResourceLocation("adventure/trade_at_world_height")); // Star Trader
        add(new ResourceLocation("husbandry/leash_all_frog_variants")); // When the Squad Hops into Town
        add(new ResourceLocation("husbandry/leash_all_frog_variants")); // With Our Powers Combined!
        add(new ResourceLocation("husbandry/froglights")); // With Our Powers Combined!
    }};

    public final Set<ResourceLocation> unreasonableAdvancements = new HashSet<>() {{
        add(new ResourceLocation("nether/all_effects")); // How Did We Get Here?
        add(new ResourceLocation("nether/all_effects")); // How Did We Get Here?
    }};

    private final Set<Long> earnedAdvancements = new HashSet<>();

    public LocationManager() {
        int initialLayer = 42000 - 1; // initial start ID is 41999 = y 256
        int buildHeight = 319; // build height
        int totalLayers = 319;
        for (int id = initialLayer; id > initialLayer-319; id--) {
            int layer = initialLayer + id;
            layerData.put("Layer " + layer, id);
        }
    }

    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event) {
        LOGGER.debug("Reload found.");
        var advancementsList = event.getServerResources().getAdvancements().advancements;
        advancementsList.getAllAdvancements().removeIf(
                advancement -> advancement.getId().getPath().startsWith("recipes/")
        );

        Style hardStyle = Style.EMPTY.withBold(true).withColor(TextColor.parseColor("#FFA500"));
        Component hardText = Component.literal(" (Hard)").withStyle(hardStyle);

        var newAdvancements = new HashMap<ResourceLocation, Advancement.Builder>();
        var advIterator = advancementsList.getAllAdvancements().iterator();
        while (advIterator.hasNext()) {
            var advancement = advIterator.next();
            if(hardAdvancements.contains(advancement.getId())) {
                advIterator.remove();
                var display = advancement.getDisplay();
                LOGGER.debug("Hard advancement " + advancement.getDisplay().getTitle().getString() + " found.");
                var title = display.getTitle().copy().append(hardText);


                var newDisplay = new DisplayInfo(display.getIcon(), title, display.getDescription(), display.getBackground(), display.getFrame(), true, false, false);
                var advancementBuilder = advancement.deconstruct().display(newDisplay);
                newAdvancements.put(advancement.getId(), advancementBuilder);
                LOGGER.debug("Hard advancement " + newDisplay.getTitle().getString() + " modified.");
            }
        }

        advancementsList.add(newAdvancements);
    }

    public int getAdvancementID(String namespacedID) {
        if (advancementData.containsKey(namespacedID))
            return advancementData.get(namespacedID);
        return 0;
    }

    public boolean hasAdvancement(int id) {
        return earnedAdvancements.contains(id);
    }

    public boolean hasAdvancement(String namespacedID) {
        return earnedAdvancements.contains(getAdvancementID(namespacedID));
    }

    public void addAdvancement(long id) {
        earnedAdvancements.add(id);
        APRandomizer.getAP().checkLocation(id);
        APRandomizer.getGoalManager().updateGoal( true);
        APRandomizer.getServer().getLevel(Level.OVERWORLD).getCapability(APCapabilities.WORLD_DATA).orElseThrow(AssertionError::new).addLocation(id);
        syncAllAdvancements();
    }

    public void resendAdvancements() {
        for (Long earnedAdvancement : earnedAdvancements) {
            APRandomizer.getAP().checkLocation(earnedAdvancement);
        }
    }

    public void syncAdvancement(Advancement a) {
        if (hasAdvancement(a.getId().toString())) {
            for (ServerPlayer serverPlayerEntity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                AdvancementProgress ap = serverPlayerEntity.getAdvancements().getOrStartProgress(a);
                if (ap.isDone())
                    continue;
                for (String remainingCriterion : ap.getRemainingCriteria()) {
                    serverPlayerEntity.getAdvancements().award(a, remainingCriterion);
                }
            }
        }
    }

    public void syncAllAdvancements() {
        for (Advancement a : getServer().getAdvancements().getAllAdvancements()) {
            syncAdvancement(a);
        }
    }

    public int getFinishedAmount() {
        return earnedAdvancements.size();
    }

    public void setCheckedAdvancements(Set<Long> checkedLocations) {
        earnedAdvancements.addAll(checkedLocations);
        WorldData data = APRandomizer.getServer().getLevel(Level.OVERWORLD).getCapability(APCapabilities.WORLD_DATA).orElseThrow(AssertionError::new);
        for (var checkedLocation : checkedLocations) {
            data.addLocation(checkedLocation);
        }

        syncAllAdvancements();
    }
}
