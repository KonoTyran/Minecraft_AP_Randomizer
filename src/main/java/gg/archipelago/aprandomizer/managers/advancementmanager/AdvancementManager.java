package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.CapabilityWorldData;
import gg.archipelago.aprandomizer.capability.WorldData;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static gg.archipelago.aprandomizer.APRandomizer.getAP;
import static gg.archipelago.aprandomizer.APRandomizer.getServer;

public class AdvancementManager {

    private final HashMap<String, Integer> advancementData = new HashMap<String, Integer>() {{
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
        put("minecraft:story/get_wood", 42080);
        put("minecraft:story/get_pickaxe", 42081);
        put("minecraft:story/hot_topic", 42082);
        put("minecraft:husbandry/bake_bread", 42083);
        put("minecraft:husbandry/the_lie", 42084);
        put("minecraft:adventure/ride_minecart", 42085);
        put("minecraft:adventure/craft_sword", 42086);
        put("minecraft:husbandry/cow_tipper", 42087);
        put("minecraft:husbandry/ride_pig", 42088);
        put("minecraft:story/overkill", 42089);
        put("minecraft:story/obtain_bookshelf", 42090);
        put("minecraft:husbandry/overpowered", 42091);
    }};

    private final Set<Integer> earnedAdvancements = new HashSet<>();


    public AdvancementManager() {
        Collection<Advancement> advancements = getServer().getAdvancements().getAllAdvancements();
        advancements.removeIf(advancement -> advancement.getId().getPath().startsWith("recipes/"));
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

    public void addAdvancement(int id) {
        earnedAdvancements.add(id);
        APRandomizer.getAP().checkLocation(id);
        APRandomizer.getGoalManager().updateGoal();
        APRandomizer.getServer().getLevel(World.OVERWORLD).getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new).addLocation(id);
    }

    public void resendAdvancements() {
        for (Integer earnedAdvancement : earnedAdvancements) {
            APRandomizer.getAP().checkLocation(earnedAdvancement);
        }
    }

    public void syncAdvancement(Advancement a) {
        if (hasAdvancement(a.getId().toString())) {
            for (ServerPlayerEntity serverPlayerEntity : APRandomizer.getServer().getPlayerList().getPlayers()) {
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

    public void setCheckedAdvancements(Set<Integer> checkedLocations) {
        earnedAdvancements.addAll(checkedLocations);
        WorldData data = APRandomizer.getServer().getLevel(World.OVERWORLD).getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new);
        for (Integer checkedLocation : checkedLocations) {
            data.addLocation(checkedLocation);
        }

        syncAllAdvancements();
    }
}
