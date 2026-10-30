package com.equilibrium.difficulty_entry_screen;

import com.equilibrium.block.ModBlockScreenTypesRegister;
import com.equilibrium.difficulty_entry.DifficultyEntryRegister;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import java.util.List;


public class DifficultyEntryScreenHandler extends AbstractContainerMenu {

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

    private final SimpleContainerData ruleValues = new SimpleContainerData(RULES.size());

    private final SimpleContainerData gateStates = new SimpleContainerData(2);


    public DifficultyEntryScreenHandler(int syncId, Inventory inventory) {
        this(syncId, inventory, ContainerLevelAccess.NULL);
    }

    public DifficultyEntryScreenHandler(int syncId, Inventory inventory, ContainerLevelAccess context) {
        super(ModBlockScreenTypesRegister.DIFFICULTY_ENTRY_SCREEN_HANDLER_MENU_TYPE, syncId);
        this.server = context.evaluate((world, pos) -> world.getServer()).orElse(null);
        this.addDataSlots(this.ruleValues);
        this.addDataSlots(this.gateStates);
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
    public boolean clickMenuButton(Player player, int index) {
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
        GameRules.Value<?> current = this.server.getGameRules().getRule(rule.key());
        if (!(current instanceof GameRules.BooleanValue booleanRule)) {
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
                this.server.getWorldData().isAllowCommands() ? 1 : 0);
        this.gateStates.set(GATE_CREATIVE_OR_SPECTATOR,
                anyCreativeOrSpectator(this.server.getPlayerList().getPlayers()) ? 1 : 0);
    }


    @Override
    public void broadcastChanges() {
        this.refreshStates();
        super.broadcastChanges();
    }


    private static boolean computeExtraUnlocked(MinecraftServer server) {
        return !server.getWorldData().isAllowCommands()
                && !anyCreativeOrSpectator(server.getPlayerList().getPlayers());
    }


    private static boolean anyCreativeOrSpectator(List<ServerPlayer> playerList) {
        for (ServerPlayer target : playerList) {
            if (target.isCreative() || target.isSpectator()) {
                return true;
            }
        }
        return false;
    }



    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return ItemStack.EMPTY;
    }
    @Override
    public boolean stillValid(Player player) {
        return true;
    }



    public record Rule(GameRules.Key<GameRules.BooleanValue> key, Section section) {

        public String id() {
            return this.key.getId();
        }

        public String nameKey() {
            return this.key.getDescriptionId();
        }

        public String descriptionKey() {
            return this.nameKey() + ".description";
        }
    }
}
