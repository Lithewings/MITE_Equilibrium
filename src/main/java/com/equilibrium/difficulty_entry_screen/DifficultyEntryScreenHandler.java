package com.equilibrium.difficulty_entry_screen;

import com.equilibrium.difficulty_entry.DifficultyEntryRegister;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.util.List;


public class DifficultyEntryScreenHandler extends ScreenHandler {

    private static final int GATE_ALLOW_COMMANDS = 0;

    private static final int GATE_CREATIVE_OR_SPECTATOR = 1;

    public enum Section {
        BASIC,
        EXTRA
    }

    public static final List<Rule> RULES = List.of(
            // Basic
            new Rule(DifficultyEntryRegister.ENABLE_CRAFTING_TIME_AND_LEVEL, Section.BASIC),
            new Rule(DifficultyEntryRegister.ENABLE_SLOW_BREAKING_SPEED, Section.BASIC),
            new Rule(DifficultyEntryRegister.ENABLE_CROP_ILLNESS, Section.BASIC),
            new Rule(DifficultyEntryRegister.ENABLE_ADVANCE_ANIMAL_AI, Section.BASIC),
            new Rule(DifficultyEntryRegister.ENABLE_RESTRICT_VILLAGE_GEN, Section.BASIC),
            new Rule(DifficultyEntryRegister.ENABLE_PHYTONUTRIENT, Section.BASIC),
            new Rule(DifficultyEntryRegister.ENABLE_BLOOD_MOON_THUNDER, Section.BASIC),
            new Rule(DifficultyEntryRegister.DISABLE_PLAYER_TELEPORT, Section.BASIC),
            new Rule(DifficultyEntryRegister.DISABLE_ALWAYS_NIGHT_VISION, Section.BASIC),
            // Extra
            new Rule(DifficultyEntryRegister.DISABLE_VILLAGE_AND_PILLAGE, Section.EXTRA),
            new Rule(DifficultyEntryRegister.ENABLE_MORE_SL_DAMAGE, Section.EXTRA),
            new Rule(DifficultyEntryRegister.ENABLE_NO_ANIMALS, Section.EXTRA),
            new Rule(DifficultyEntryRegister.ENABLE_MORE_RAIN_WEATHER, Section.EXTRA),
            new Rule(DifficultyEntryRegister.DISABLE_CROP_GROW, Section.EXTRA),
            new Rule(DifficultyEntryRegister.ENABLE_UNIVERSAL_AGGRO, Section.EXTRA),
            new Rule(DifficultyEntryRegister.ENABLE_ANVIL_LEVEL, Section.EXTRA),
            new Rule(DifficultyEntryRegister.ENABLE_ADVANCED_ENCHANTING_TABLE, Section.EXTRA)
    );

    private final MinecraftServer server;

    private final ArrayPropertyDelegate ruleValues = new ArrayPropertyDelegate(RULES.size());

    private final ArrayPropertyDelegate gateStates = new ArrayPropertyDelegate(2);


    public DifficultyEntryScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY);
    }

    public DifficultyEntryScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context) {
        super(DifficultyEntryScreenRegistrations.SCREEN_TYPE, syncId);
        this.server = context.get((world, pos) -> world.getServer()).orElse(null);
        this.addProperties(this.ruleValues);
        this.addProperties(this.gateStates);
        // 客户端（server == null）下面两句空转，初值来自属性包
        this.pullValues();
        this.refreshStates();
    }




    /** 某条规则在开屏快照里的值。 */
    public int valueAt(int index) {
        return this.ruleValues.get(index);
    }



    //服务端每 tick 刷新
    public boolean isExtraUnlocked() {
        return !this.isCommandsAllowed() && !this.hasCreativeOrSpectator();
    }

    public boolean isCommandsAllowed() {
        return this.gateStates.get(GATE_ALLOW_COMMANDS) != 0;
    }

    public boolean hasCreativeOrSpectator() {
        return this.gateStates.get(GATE_CREATIVE_OR_SPECTATOR) != 0;
    }



    @Override
    public boolean onButtonClick(PlayerEntity player, int index) {
        if (index < 0 || index >= RULES.size()) {
            return false;
        }
        return apply(RULES.get(index));
    }


    private boolean apply(Rule rule) {
        if (this.server == null) {
            return false;
        }
        // 按此刻的事实判定，而不是开屏时的快照：中途有人切创造模式也要挡住
        if (rule.section() == Section.EXTRA && !computeExtraUnlocked(this.server)) {
            return false;
        }
        GameRules.Rule<?> current = this.server.getGameRules().get(rule.key());
        if (!(current instanceof GameRules.BooleanRule booleanRule)) {
            return false;
        }
        //直接反转布尔规则的布尔值
        booleanRule.set(!booleanRule.get(), this.server);

        return true;
    }





    private void pullValues() {
        if (this.server == null) {
            return;
        }
        GameRules gameRules = this.server.getGameRules();
        for (int i = 0; i < RULES.size(); i++) {
            this.ruleValues.set(i, gameRules.getBoolean(RULES.get(i).key()) ? 1 : 0);
        }
    }


    private void refreshStates() {
        if (this.server == null) {
            return;
        }
        this.gateStates.set(GATE_ALLOW_COMMANDS,
                this.server.getSaveProperties().areCommandsAllowed() ? 1 : 0);
        this.gateStates.set(GATE_CREATIVE_OR_SPECTATOR,
                anyCreativeOrSpectator(this.server.getPlayerManager().getPlayerList()) ? 1 : 0);
    }


    @Override
    public void sendContentUpdates() {
        this.refreshStates();
        super.sendContentUpdates();
    }


    private static boolean computeExtraUnlocked(MinecraftServer server) {
        return !server.getSaveProperties().areCommandsAllowed()
                && !anyCreativeOrSpectator(server.getPlayerManager().getPlayerList());
    }


    private static boolean anyCreativeOrSpectator(List<ServerPlayerEntity> playerList) {
        for (ServerPlayerEntity target : playerList) {
            if (target.isCreative() || target.isSpectator()) {
                return true;
            }
        }
        return false;
    }



    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }



    public record Rule(GameRules.Key<GameRules.BooleanRule> key, Section section) {

        public String id() {
            return this.key.getName();
        }

        public String nameKey() {
            return this.key.getTranslationKey();
        }

        public String descriptionKey() {
            return this.nameKey() + ".description";
        }
    }
}
