package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.data.WorldData;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

import static gg.archipelago.aprandomizer.APRandomizer.*;

public class AdvancementManager {

    private final HashMap<String, Long> advancementData = new HashMap<>() {{
        put("minecraft:nether/obtain_crying_obsidian", 42000L);
        put("minecraft:nether/distract_piglin", 42001L);
        put("minecraft:story/obtain_armor", 42002L);
        put("minecraft:adventure/very_very_frightening", 42003L);
        put("minecraft:story/lava_bucket", 42004L);
        put("minecraft:end/kill_dragon", 42005L);
        put("minecraft:nether/all_potions", 42006L);
        put("minecraft:husbandry/tame_an_animal", 42007L);
        put("minecraft:nether/create_beacon", 42008L);
        put("minecraft:story/deflect_arrow", 42009L);
        put("minecraft:story/iron_tools", 42010L);
        put("minecraft:nether/brew_potion", 42011L);
        put("minecraft:end/dragon_egg", 42012L);
        put("minecraft:husbandry/fishy_business", 42013L);
        put("minecraft:nether/explore_nether", 42014L);
        put("minecraft:nether/ride_strider", 42015L);
        put("minecraft:adventure/sniper_duel", 42016L);
        put("minecraft:nether/root", 42017L);
        put("minecraft:end/levitate", 42018L);
        put("minecraft:nether/all_effects", 42019L);
        put("minecraft:adventure/bullseye", 42020L);
        put("minecraft:nether/get_wither_skull", 42021L);
        put("minecraft:husbandry/bred_all_animals", 42022L);
        put("minecraft:story/mine_stone", 42023L);
        put("minecraft:adventure/two_birds_one_arrow", 42024L);
        put("minecraft:story/enter_the_nether", 42025L);
        put("minecraft:adventure/whos_the_pillager_now", 42026L);
        put("minecraft:story/upgrade_tools", 42027L);
        put("minecraft:husbandry/tactical_fishing", 42028L);
        put("minecraft:story/cure_zombie_villager", 42029L);
        put("minecraft:end/find_end_city", 42030L);
        put("minecraft:story/form_obsidian", 42031L);
        put("minecraft:end/enter_end_gateway", 42032L);
        put("minecraft:nether/obtain_blaze_rod", 42033L);
        put("minecraft:nether/loot_bastion", 42034L);
        put("minecraft:adventure/shoot_arrow", 42035L);
        put("minecraft:husbandry/silk_touch_nest", 42036L);
        put("minecraft:adventure/arbalistic", 42037L);
        put("minecraft:end/respawn_dragon", 42038L);
        put("minecraft:story/smelt_iron", 42039L);
        put("minecraft:nether/charge_respawn_anchor", 42040L);
        put("minecraft:story/shiny_gear", 42041L);
        put("minecraft:end/elytra", 42042L);
        put("minecraft:adventure/summon_iron_golem", 42043L);
        put("minecraft:nether/return_to_sender", 42044L);
        put("minecraft:adventure/sleep_in_bed", 42045L);
        put("minecraft:end/dragon_breath", 42046L);
        put("minecraft:adventure/root", 42047L);
        put("minecraft:adventure/kill_all_mobs", 42048L);
        put("minecraft:story/enchant_item", 42049L);
        put("minecraft:adventure/voluntary_exile", 42050L);
        put("minecraft:story/follow_ender_eye", 42051L);
        put("minecraft:end/root", 42052L);
        put("minecraft:husbandry/obtain_netherite_hoe", 42053L);
        put("minecraft:adventure/totem_of_undying", 42054L);
        put("minecraft:adventure/kill_a_mob", 42055L);
        put("minecraft:adventure/adventuring_time", 42056L);
        put("minecraft:husbandry/plant_seed", 42057L);
        put("minecraft:nether/find_bastion", 42058L);
        put("minecraft:adventure/hero_of_the_village", 42059L);
        put("minecraft:nether/obtain_ancient_debris", 42060L);
        put("minecraft:nether/create_full_beacon", 42061L);
        put("minecraft:nether/summon_wither", 42062L);
        put("minecraft:husbandry/balanced_diet", 42063L);
        put("minecraft:nether/fast_travel", 42064L);
        put("minecraft:husbandry/root", 42065L);
        put("minecraft:nether/use_lodestone", 42066L);
        put("minecraft:husbandry/safely_harvest_honey", 42067L);
        put("minecraft:adventure/trade", 42068L);
        put("minecraft:nether/uneasy_alliance", 42069L);
        put("minecraft:story/mine_diamond", 42070L);
        put("minecraft:nether/find_fortress", 42071L);
        put("minecraft:adventure/throw_trident", 42072L);
        put("minecraft:story/root", 42073L);
        put("minecraft:adventure/honey_block_slide", 42074L);
        put("minecraft:adventure/ol_betsy", 42075L);
        put("minecraft:nether/netherite_armor", 42076L);
        put("minecraft:story/enter_the_end", 42077L);
        put("minecraft:husbandry/breed_an_animal", 42078L);
        put("minecraft:husbandry/complete_catalogue", 42079L);
        put("aprandomizer:archipelago/get_wood", 42080L);
        put("aprandomizer:archipelago/get_pickaxe", 42081L);
        put("aprandomizer:archipelago/hot_topic", 42082L);
        put("aprandomizer:archipelago/bake_bread", 42083L);
        put("aprandomizer:archipelago/the_lie", 42084L);
        put("aprandomizer:archipelago/ride_minecart", 42085L);
        put("aprandomizer:archipelago/craft_sword", 42086L);
        put("aprandomizer:archipelago/cow_tipper", 42087L);
        put("aprandomizer:archipelago/ride_pig", 42088L);
        put("aprandomizer:archipelago/overkill", 42089L);
        put("aprandomizer:archipelago/obtain_bookshelf", 42090L);
        put("aprandomizer:archipelago/overpowered", 42091L);
        put("minecraft:husbandry/wax_on", 42092L);
        put("minecraft:husbandry/wax_off", 42093L);
        put("minecraft:husbandry/axolotl_in_a_bucket", 42094L);
        put("minecraft:husbandry/kill_axolotl_target", 42095L);
        put("minecraft:adventure/spyglass_at_parrot", 42096L);
        put("minecraft:adventure/spyglass_at_ghast", 42097L);
        put("minecraft:adventure/spyglass_at_dragon", 42098L);
        put("minecraft:adventure/lightning_rod_with_villager_no_fire", 42099L);
        put("minecraft:adventure/walk_on_powder_snow_with_leather_boots", 42100L);
        put("minecraft:husbandry/make_a_sign_glow", 42101L);
        put("minecraft:husbandry/ride_a_boat_with_a_goat", 42102L);
        put("minecraft:adventure/fall_from_world_height", 42103L);
        put("minecraft:nether/ride_strider_in_overworld_lava", 42104L);
        put("minecraft:adventure/play_jukebox_in_meadows", 42105L);
        put("minecraft:adventure/trade_at_world_height", 42106L);
        // 1.19 advancements
        put("minecraft:husbandry/allay_deliver_cake_to_note_block", 42107L);
        put("minecraft:husbandry/tadpole_in_a_bucket", 42108L);
        put("minecraft:adventure/kill_mob_near_sculk_catalyst", 42109L);
        put("minecraft:adventure/avoid_vibration", 42110L);
        put("minecraft:husbandry/leash_all_frog_variants", 42111L);
        put("minecraft:husbandry/froglights", 42112L);
        put("minecraft:husbandry/allay_deliver_item_to_player", 42113L);

    }};

    public static final Set<ResourceLocation> hardAdvancements = new HashSet<>() {{
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

    public AdvancementManager() {

    }


    public Long getAdvancementID(String namespacedID) {
        if (advancementData.containsKey(namespacedID))
            return advancementData.get(namespacedID);
        return 0L;
    }

    public boolean hasAdvancement(Long id) {
        return earnedAdvancements.contains(id);
    }

    public boolean hasAdvancement(String namespacedID) {
        return earnedAdvancements.contains(getAdvancementID(namespacedID));
    }

    public void addAdvancement(long id) {
        earnedAdvancements.add(id);
        APRandomizer.getAP().checkLocation(id);
        APRandomizer.getGoalManager().updateGoal( true);
        APRandomizer.getWorldData().addLocation(id);
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
        if (APRandomizer.getRecipeManager().hasReceived(a.getId())) {
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
        WorldData data = APRandomizer.getWorldData();
        for (var checkedLocation : checkedLocations) {
            data.addLocation(checkedLocation);
        }

        syncAllAdvancements();
    }
}
