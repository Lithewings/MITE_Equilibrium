package com.equilibrium.difficulty_entry_screen;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
//Powered by Deepseek V4 pro
public class DifficultyEntryScreen extends AbstractContainerScreen<DifficultyEntryScreenHandler> {

    /** 行高。列表用固定 itemHeight 布局（{@code Entry} 没有 getHeight），所以只能统一取一个够两行名称的值。 */
    private static final int ROW_HEIGHT = 30;
    private static final int NAME_LINE_HEIGHT = 10;
    /** 名称最多折几行，再多就交给 tooltip。 */
    private static final int MAX_NAME_LINES = 2;

    private static final int HEADER_HEIGHT = 30;
    private static final int FOOTER_HEIGHT = 28;
    private static final int SIDE_MARGIN = 8;
    private static final int COLUMN_GAP = 10;
    private static final int COLUMN_TITLE_Y = 20;
    private static final int NOTICE_LINE_HEIGHT = 11;

    /** 「开 / 关」按钮的宽度：只要放得下两个字，剩下的全给文字区。 */
    private static final int SWITCH_WIDTH = 56;
    /** 按钮与文字之间的最小间隔。 */
    private static final int SWITCH_GAP = 10;

    private static final Component BASIC_TITLE = Component.literal("Basic");
    private static final Component EXTRA_TITLE = Component.literal("Extra");

    /** 三句状态提示走语言文件（键见 assets/miteequilibrium/lang/），不再硬编码中文。 */
    private static final Component NOTICE_UNLOCKED = Component.translatable("difficulty_entry_screen.notice.unlocked");
    private static final Component NOTICE_COMMANDS = Component.translatable("difficulty_entry_screen.notice.commands");
    private static final Component NOTICE_CREATIVE = Component.translatable("difficulty_entry_screen.notice.creative");

    private RuleList basicList;
    private RuleList extraList;

    public DifficultyEntryScreen(DifficultyEntryScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        // 把「容器尺寸」声明成整个屏幕。本屏是全屏自绘的，但继承的 HandledScreen 默认
        // backgroundWidth/Height = 176×166，所有外部代码（JEI 的侧栏定位、点击外部判定）
        // 都按这个数字摆位——于是它们会以为这里有个 176 宽的小容器，把侧栏压到规则行上。
        // 声明成真实尺寸后，外部拿到的就是「这屏占满整个窗口」。
        this.imageWidth = this.width;
        this.imageHeight = this.height;
        // super.init() 已经用旧的 176×166 算过 x/y，这里按新尺寸重算一次（结果恒为 0）。
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        int listHeight = Math.max(ROW_HEIGHT * 2, this.height - HEADER_HEIGHT - FOOTER_HEIGHT);
        int columnWidth = Math.max(120, (this.width - SIDE_MARGIN * 2 - COLUMN_GAP) / 2);
        int leftX = SIDE_MARGIN;
        int rightX = leftX + columnWidth + COLUMN_GAP;

        this.basicList = new RuleList(this.minecraft, columnWidth, listHeight, HEADER_HEIGHT,
                DifficultyEntryScreenHandler.Section.BASIC);
        this.basicList.setX(leftX);
        this.addRenderableWidget(this.basicList);

        this.extraList = new RuleList(this.minecraft, columnWidth, listHeight, HEADER_HEIGHT,
                DifficultyEntryScreenHandler.Section.EXTRA);
        this.extraList.setX(rightX);
        this.addRenderableWidget(this.extraList);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - FOOTER_HEIGHT + 6, 200, 20)
                .build());
    }

    /** 全屏列表自带底色与滚动条，不画原版容器背景。 */
    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
    }

    /**
     * 原版 {@code HandledScreen.drawForeground} 用 0x404040 深灰画两个标签：
     * 容器标题（画在容器框的 (8, 6)）和玩家物品栏标题（画在 (8, 72)，
     * 也就是 {@code PlayerInventory#getDisplayName()} = 「物品栏」）。
     * 这两个坐标是相对 176×166 容器框的，可这里是全屏自绘、压根没有容器框，
     * 于是两行灰字就飘在模糊背景上——所以留空，标题由 {@link #render} 自己画在顶部居中。
     */
    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        // 贴值只在 handler 值发生变化时发生（开屏时属性到达那一次），点击后不会被顶回去；
        // 门控则每帧重算——属性包是 init 之后才到的，缓存下来就会一直是「解锁」。
        this.basicList.syncValues(this.menu);
        this.extraList.syncValues(this.menu);

        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredString(this.font, this.title, this.width / 2, 13, 0xFFFFFFFF);
        drawColumnTitle(context, BASIC_TITLE, this.basicList);
        drawColumnTitle(context, EXTRA_TITLE, this.extraList);

        // 放在最后画：tooltip 要盖在行、按钮、标题之上
        this.drawHoveredTooltip(context, mouseX, mouseY);
    }

    /** 两栏里最多只有一行被 hover，找到就画它的完整内容并结束。 */
    private void drawHoveredTooltip(GuiGraphics context, int mouseX, int mouseY) {
        for (RuleList list : List.of(this.basicList, this.extraList)) {
            for (RuleList.Row row : list.children()) {
                if (row.drawTooltip(context, mouseX, mouseY)) {
                    return;
                }
            }
        }
    }

    private void drawColumnTitle(GuiGraphics context, Component title, RuleList list) {
        context.drawCenteredString(this.font, title,
                list.getX() + list.getWidth() / 2, COLUMN_TITLE_Y, 0xFFFFFFFF);
    }

    /**
     * {@code HandledScreen#mouseDragged} 的字节码末尾是无条件 {@code return true}，整段里
     * 一次 {@code Screen#mouseDragged} 都没有调用 —— 也就是说拖动事件永远到不了 drawableChild，
     * 屏里的列表压根收不到。原版那套拖物品逻辑本屏用不上（没有槽位），这里先自己转发。
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (GuiEventListener child : this.children()) {
            if (child.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /** 同上：{@code HandledScreen#mouseReleased} 也不转发，缺这一句滚动条的拖动状态就清不掉。 */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (GuiEventListener child : this.children()) {
            if (child.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /** 控件动了：只发「点了哪一行」，新值由服务端取反后写入。 */
    private void sendClick(int index) {
        Minecraft client = this.minecraft;
        if (client == null || client.gameMode == null) {
            return;
        }
        client.gameMode.handleInventoryButtonClick(this.menu.containerId, index);
    }

    /** 有语言条目用语言条目，没有就直接显示规则 id（如 {@code enableCropIllness}）。 */
    private static Component ruleName(DifficultyEntryScreenHandler.Rule rule) {
        return I18n.exists(rule.nameKey())
                ? Component.translatable(rule.nameKey())
                : Component.literal(rule.id());
    }

    /** 没有说明条目时返回 null，行渲染会跳过第二行。 */
    private static Component ruleDescription(DifficultyEntryScreenHandler.Rule rule) {
        return I18n.exists(rule.descriptionKey()) ? Component.translatable(rule.descriptionKey()) : null;
    }

    /** 一行里留给名称的宽度：行宽减掉开关、间隔和左侧 4px 内边距。 */
    private static int textLimit(int rowWidth) {
        return Math.max(40, rowWidth - SWITCH_WIDTH - SWITCH_GAP - 4);
    }

    /**
     * 按行宽把名称折成最多 {@link #MAX_NAME_LINES} 行。
     *
     * <p>英文界面下单行只放得下二十来个字符，{@code Enable Advanced Enchanting Table} 这种必然被切；
     * 折两行基本能放下全部条目，再长就只留前两行，完整名称交给 tooltip。</p>
     */
    private static List<FormattedCharSequence> wrapName(Font textRenderer, Component name, int maxWidth) {
        List<FormattedCharSequence> lines = textRenderer.split(name, Math.max(1, maxWidth));
        return lines.size() <= MAX_NAME_LINES ? lines : List.copyOf(lines.subList(0, MAX_NAME_LINES));
    }

    /**
     * 一栏：{@code Extra} 栏先插一行只读提示（锁定原因），再是受门控的规则行。
     * 宽度由构造时给定，位置由 {@code setX} 决定，所以两栏能并排。
     */
    private final class RuleList extends ContainerObjectSelectionList<RuleList.Row> {

        RuleList(Minecraft client, int width, int height, int y, DifficultyEntryScreenHandler.Section section) {
            super(client, width, height, y, ROW_HEIGHT);

            if (section == DifficultyEntryScreenHandler.Section.EXTRA) {
                this.addEntry(new NoticeRow());
            }

            List<DifficultyEntryScreenHandler.Rule> rules = DifficultyEntryScreenHandler.RULES;
            for (int i = 0; i < rules.size(); i++) {
                if (rules.get(i).section() == section) {
                    this.addEntry(new RuleRow(i, rules.get(i)));
                }
            }
        }

        /** 把 handler 的值与门控同步到每一行：值是边沿触发，门控每帧都写；顺带清掉上帧的「画过」标记。 */
        void syncValues(DifficultyEntryScreenHandler handler) {
            for (Row row : this.children()) {
                row.renderedThisFrame = false;
                row.sync(handler);
            }
        }

        /** 行宽跟着列宽走，右侧留出 8px 给滚动条（条占 right-8 .. right-2，与行不重叠）。 */
        @Override
        public int getRowWidth() {
            return Math.max(100, this.getWidth() - 16);
        }

        // ======================== 滚动条 ========================
        // 原版把条槽算在 getBorderBoxRight() + 10，而 borderBoxRight = getX() + width/2 + rowWidth/2，
        // 行宽取 getWidth()-12 之后这个位置落在列表右边界**外面 4px**；可 mouseClicked 头一句
        // 又是 if (!isMouseOver) return false，而 isMouseOver 只认到 getRight() 为止。
        // 于是条画得出来、点上去却什么也不发生。这里自己接管：条放回栏内，点击与拖动都按比例算。

        private static final int SCROLLBAR_WIDTH = 6;
        /** 条槽左右各放宽的像素，免得 6px 太细不好点。 */
        private static final int SCROLLBAR_PADDING = 2;

        private boolean draggingScrollbar;

        @Override
        protected int getScrollbarPosition() {
            return this.getRight() - SCROLLBAR_WIDTH - 2;
        }

        private boolean isOverScrollbar(double mouseX, double mouseY) {
            if (!this.scrollbarVisible()) {
                return false;
            }
            int barX = this.getScrollbarPosition();
            return mouseX >= barX - SCROLLBAR_PADDING
                    && mouseX < barX + SCROLLBAR_WIDTH + SCROLLBAR_PADDING
                    && mouseY >= this.getY()
                    && mouseY <= this.getBottom();
        }

        /** 滑块高度，与父类 renderWidget 里画条时的算法逐字一致（都用 height，不减 4）。 */
        private int thumbHeight() {
            int viewport = this.getHeight();
            int maxPosition = Math.max(1, this.getMaxPosition());
            return Mth.clamp((int) ((float) (viewport * viewport) / maxPosition), 32, viewport - 8);
        }

        /**
         * 让滑块中心跟着鼠标走：点哪跳哪，拖动也走这里。
         *
         * <p>父类画条时滑块顶 = {@code getY() + scrollAmount * (height - thumb) / maxScroll}，
         * 这里就按这个式子反解 scrollAmount。</p>
         */
        private void scrollToMouse(double mouseY) {
            int viewport = this.getHeight();
            int thumb = this.thumbHeight();
            int travel = viewport - thumb;
            if (travel <= 0) {
                return;
            }
            double thumbTop = mouseY - this.getY() - thumb / 2.0;
            this.setScrollAmount(Mth.clamp(thumbTop / travel, 0.0, 1.0) * this.getMaxScroll());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // 每次按下先归零：mouseReleased 即使没送到这里，也不会留下「幽灵拖动」
            this.draggingScrollbar = false;
            if (button == 0 && this.isOverScrollbar(mouseX, mouseY)) {
                this.draggingScrollbar = true;
                this.scrollToMouse(mouseY);
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (this.draggingScrollbar && button == 0) {
                this.scrollToMouse(mouseY);
                return true;
            }
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (this.draggingScrollbar) {
                this.draggingScrollbar = false;
                return true;
            }
            return super.mouseReleased(mouseX, mouseY, button);
        }

        /** 行基类：默认不持有子控件、不画东西。 */
        abstract class Row extends Entry<Row> {

            /**
             * 本帧是否真的被画过。{@code EntryListWidget#renderList} 有视口裁剪
             * （{@code rowBottom < getY()} 或 {@code rowTop > getBottom()} 就跳过），
             * 被跳过的行不会执行 {@code render}，它的 {@code hovered} 就停在上一次的值上。
             * 每帧由 {@link #syncValues} 清零，所以「滚出视口却还弹着 tooltip」不会发生。
             */
            boolean renderedThisFrame;

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of();
            }



            void sync(DifficultyEntryScreenHandler handler) {
            }

            @Override
            public void render(GuiGraphics context, int index, int y, int x, int width, int height,
                               int mouseX, int mouseY, boolean hovered, float delta) {
            }

            /** 鼠标停在本行时画提示，返回 true 表示已画（屏幕层就不再往下找）。 */
            boolean drawTooltip(GuiGraphics context, int mouseX, int mouseY) {
                return false;
            }
        }

        /** Extra 栏第一行：只读。解锁时一行绿字，锁定时把不满足的条件逐条列出。 */
        final class NoticeRow extends Row {

            @Override
            public void render(GuiGraphics context, int index, int y, int x, int width, int height,
                               int mouseX, int mouseY, boolean hovered, float delta) {
                Font textRenderer = DifficultyEntryScreen.this.font;
                DifficultyEntryScreenHandler handler = DifficultyEntryScreen.this.menu;
                // 两个门控槽各读一次，别在循环里反复问
                boolean commands = handler.isCommandsAllowed();
                boolean creative = handler.hasCreativeOrSpectator();

                List<Component> notices = new ArrayList<>(2);
                if (commands) {
                    notices.add(NOTICE_COMMANDS);
                }
                if (creative) {
                    notices.add(NOTICE_CREATIVE);
                }
                boolean locked = !notices.isEmpty();
                if (!locked) {
                    notices.add(NOTICE_UNLOCKED);
                }

                // 这里必须折行，不能像以前那样 trimToWidth：同一句中文 17 字，英文要 29 个字符，
                // 窄窗口下（栏宽约 200）一行只放得下三十来个字符，截断后语义就丢了。
                // 行高只装得下 ROW_HEIGHT / NOTICE_LINE_HEIGHT 行，超出部分截掉，别画到相邻行上去。
                int maxLines = ROW_HEIGHT / NOTICE_LINE_HEIGHT;
                int linesPerNotice = Math.max(1, maxLines / notices.size());
                int maxWidth = Math.max(20, width - 8);

                List<FormattedCharSequence> lines = new ArrayList<>(maxLines);
                for (Component notice : notices) {
                    List<FormattedCharSequence> wrapped = textRenderer.split(notice, maxWidth);
                    int take = Math.min(wrapped.size(), linesPerNotice);
                    for (int i = 0; i < take; i++) {
                        lines.add(wrapped.get(i));
                    }
                }

                int color = locked ? 0xFFFF8080 : 0xFF55FF55;
                int top = y + (ROW_HEIGHT - lines.size() * NOTICE_LINE_HEIGHT) / 2;
                for (int i = 0; i < lines.size(); i++) {
                    FormattedCharSequence line = lines.get(i);
                    context.drawString(textRenderer, line,
                            x + (width - textRenderer.width(line)) / 2,
                            top + i * NOTICE_LINE_HEIGHT,
                            color);
                }
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of();
            }
        }

        /**
         * 一条规则：名称在左（最多折两行），开关贴右。
         *
         * <p>行内不再放说明文字——{@code gamerule.<name>.description} 一条都没有，那条永远是空行；
         * 而英文名动辄三十多个字符，单行必被截。所以名称允许折两行，完整内容（名称 / id / 说明）
         * 一律还给 tooltip。</p>
         */
        final class RuleRow extends Row {

            private final int index;
            private final boolean gated;
            private final Component name;
            private final Component description;
            /** 规则 id；行内被截断时，tooltip 里用它兜住“这到底是哪条”。 */
            private final Component id;
            /** 按行宽预折好的名称，1–2 行。 */
            private final List<FormattedCharSequence> nameLines;
            private final CycleButton<Boolean> widget;
            private boolean unlocked = true;
            /** 是否已经从 handler 贴过一次值；配合 {@link #applied} 让贴值只在值变化时发生。 */
            private boolean valueApplied;
            private int applied;
            /** 上一帧是否被鼠标悬停，由 {@link #render} 更新。 */
            private boolean hovered;

            RuleRow(int index, DifficultyEntryScreenHandler.Rule rule) {
                this.index = index;
                this.gated = rule.section() == DifficultyEntryScreenHandler.Section.EXTRA;
                this.name = ruleName(rule);
                this.description = ruleDescription(rule);
                this.id = Component.literal(rule.id()).withStyle(ChatFormatting.GRAY);
                this.nameLines = wrapName(DifficultyEntryScreen.this.font, this.name,
                        textLimit(RuleList.this.getRowWidth()));
                // omitKeyText：不要「标签: 」前缀，只留 开 / 关
                this.widget = CycleButton.onOffBuilder(DifficultyEntryScreen.this.menu.valueAt(index) != 0)
                        .displayOnlyValue()
                        .create(0, 0, SWITCH_WIDTH, 20, CommonComponents.EMPTY,
                                (button, value) -> sendClick(this.index));
            }

            @Override
            void sync(DifficultyEntryScreenHandler handler) {
                // 门控每帧从属性槽重算，不能缓存 init() 里读到的结果：属性包是在界面建好之后
                // 才到的，init 那一刻两个门控槽都还是 0（= 解锁）。缓存下来就会一直以为能改，
                // 而提示行读的是实时值，于是表现为「提示已锁定，但开关照样点得动」。
                this.unlocked = !this.gated || handler.isExtraUnlocked();
                this.widget.active = this.unlocked;

                // 值只跟随 handler 的变化：开屏时属性到达会贴一次，之后点击不再被顶回去
                int raw = handler.valueAt(this.index);
                if (this.valueApplied && raw == this.applied) {
                    return;
                }
                this.widget.setValue(raw != 0);
                this.applied = raw;
                this.valueApplied = true;
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of(this.widget);
            }


            @Override
            public void render(GuiGraphics context, int index, int y, int x, int width, int height,
                               int mouseX, int mouseY, boolean hovered, float delta) {
                this.renderedThisFrame = true;
                int limit = textLimit(width);
                // 只有指针落在文字区（左侧）才算悬停，落在开关上时不弹提示，免得挡住按钮
                this.hovered = hovered && mouseX < x + 4 + limit;
                this.widget.setX(x + width - SWITCH_WIDTH - 2);
                this.widget.setY(y + (height - this.widget.getHeight()) / 2);
                this.widget.render(context, mouseX, mouseY, delta);

                Font textRenderer = DifficultyEntryScreen.this.font;
                int color = this.unlocked ? 0xFFFFFFFF : 0xFF7F7F7F;
                int textY = y + (height - this.nameLines.size() * NAME_LINE_HEIGHT) / 2;
                for (FormattedCharSequence line : this.nameLines) {
                    context.drawString(textRenderer, line, x + 4, textY, color);
                    textY += NAME_LINE_HEIGHT;
                }
            }

            /**
             * 行内只放得下被截断的简写，完整内容放这里：名称、规则 id、说明三条。
             * id 单独一行是因为它同时是 {@code /gamerule} 的参数名，对着控制台能直接用。
             */
            @Override
            boolean drawTooltip(GuiGraphics context, int mouseX, int mouseY) {
                // renderedThisFrame：本帧被视口裁掉的行不弹提示——它的 hovered 是上一帧的残留，
                // 滚轮把行滚出视口后鼠标并没有移动，光看 hovered 会在这里弹出一条错位的提示。
                if (!this.hovered || !this.renderedThisFrame) {
                    return false;
                }
                List<Component> lines = new ArrayList<>(3);
                lines.add(this.name);
                lines.add(this.id);
                if (this.description != null) {
                    lines.add(this.description);
                }
                context.renderComponentTooltip(DifficultyEntryScreen.this.font, lines, mouseX, mouseY);
                return true;
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of();
            }
        }
    }
}
