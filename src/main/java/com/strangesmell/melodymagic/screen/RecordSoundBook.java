package com.strangesmell.melodymagic.screen;

import com.strangesmell.melodymagic.api.RecordUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.KEY2EFFECT;

public class RecordSoundBook extends Screen {
    private static final Component PLAY = Component.translatable("melodymagic.recode_play");
    private static final Component SELECT = Component.translatable("melodymagic.recode_select");
    private static final Component NEXT = Component.translatable("melodymagic.recode_next");
    private static final Component PREVIOUS = Component.translatable("melodymagic.recode_previous");
    private static final Component EFFECTSCREEN = Component.translatable("melodymagic.effect_screen");
    protected EditBox nameEdit;
    protected int page;
    protected int searchPage;
    protected String searchName;
    protected Button[] buttons=new Button[10];
    protected Button deleteButton1;
    protected Button deleteButton2;
    protected Button deleteButton3;
    protected Button deleteButton4;
    protected Button deleteButton5;
    protected Button deleteButton6;
    protected Button deleteButton7;
    protected Button deleteButton8;
    protected Button deleteButton9;
    protected Button deleteButton10;

    protected Button openEffectButton;
    protected Button nextButton;
    protected Button previousButton;
    protected List<SoundEvent> soundEventList = new ArrayList<>();
    protected List<String> effectList= new ArrayList<>();
    protected List<String> subList= new ArrayList<>();
    protected Player player;

    public RecordSoundBook( Player  player_ ) {
        super(Component.empty());
        page=0;
        searchPage=0;
        player = player_;
        RecordUtil.loadSoundKinds(player_,soundEventList);
        RecordUtil.loadEffectKinds(player_,effectList);
        RecordUtil.loadSubKinds(player_,subList);
    }
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 然后是窗口小部件，如果这是Screen的直接子项
        this.nameEdit.render( graphics,  mouseX,  mouseY,  partialTick);
        this.openEffectButton.render(graphics, mouseX, mouseY, partialTick);
        // 在小部件之后渲染的内容（工具提示）
        graphics.drawCenteredString(this.font, SELECT, 20, 15, 16777215);
        String name = this.nameEdit.getValue();

        if(name.equals("")){
            int size = soundEventList.size();

            if(page*10+10<size) nextButton.render( graphics,  mouseX,  mouseY,  partialTick);
            if(page>0) previousButton.render(graphics,  mouseX,  mouseY,  partialTick);

            for(int i = 0; i+page*10<size&&i<10;i++){
                this.buttons[i].render( graphics,  mouseX,  mouseY,  partialTick);
                String soundSub = subList.get(page*10+i);
                graphics.drawString(this.font, Component.translatable(soundSub), 30, this.height / 8 + 20 * i+3 , 16777215 );
            }
            graphics.drawString(this.font,subList.size() +" / "+ BuiltInRegistries.SOUND_EVENT.size(), (this.width- this.font.width(subList.size() +" / "+ BuiltInRegistries.SOUND_EVENT.size()))/2, this.height -80, 16777215 );

        }else{
            List<String> values= new ArrayList<>();
            for (String s : subList) if (s.contains(name)) values.add(s);

            int size = values.size();
            if(searchPage*10+10<size) nextButton.render( graphics,  mouseX,  mouseY,  partialTick);
            if(searchPage>0) previousButton.render(graphics,  mouseX,  mouseY,  partialTick);

            for(int i = 0; i+page*10<size&&i<10;i++){
                this.buttons[i].render( graphics,  mouseX,  mouseY,  partialTick);
                graphics.drawString(this.font, Component.translatable(values.get(page*10+i)), 30, this.height / 8 + 20 * i +3, 16777215 );
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        searchName="";
        page=0;
        if(player!=null){
            buttons[0] = deleteButton1;
            buttons[1] = deleteButton2;
            buttons[2] = deleteButton3;
            buttons[3] = deleteButton4;
            buttons[4] = deleteButton5;
            buttons[5] = deleteButton6;
            buttons[6] = deleteButton7;
            buttons[7] = deleteButton8;
            buttons[8] = deleteButton9;
            buttons[9] = deleteButton10;
            for(int i = 0; i<10;i++){
                int[] index = new int[1];
                index[0] = i;
                buttons[i]= this.addRenderableWidget(Button.builder(PLAY, (p_97691_) -> {
                    this.onDone(index[0]);//pos(p_254166_, p_253872_).size(
                }).bounds(this.width - 60, this.height / 8 + 20 * i, 30, 17).build());
            }
        }

        openEffectButton = this.addRenderableWidget(Button.builder(EFFECTSCREEN, (a) -> {
            this.openEffectScreen();
        }).bounds(this.width/2 - 15,  this.height -60, 30, 20).build());


        nextButton = this.addRenderableWidget(Button.builder(NEXT, (b) -> this.nextDone()).bounds(this.width-60,  this.height -60, 30, 20).build());

        previousButton = this.addRenderableWidget(Button.builder(PREVIOUS, (p_97691_) -> {
            this.previousDone();//pos(p_254166_, p_253872_).size(
        }).bounds(30 , this.height -60, 30, 20).build());


        this.nameEdit = new EditBox(this.font, this.width / 2 - 150, 10, 300, 15, Component.translatable("mcspeed.command"));
        this.nameEdit.setMaxLength(32500);
        this.addRenderableWidget(nameEdit);
    }


    protected void nextDone() {
        if(searchName.equals("")) page++;
        else searchPage++;
    }
    protected void previousDone() {
        if(searchName.equals("")) page--;
        else searchPage--;
    }
    //按键的回调函数
    protected void onDone(int i) {
        if(soundEventList.size()>page*10+i) player.playSound( soundEventList.get(page*10+i),1,1);
    }

    protected void openEffectScreen(){
        RecordEffectBook effectBook = new RecordEffectBook(player);
        Minecraft.getInstance().setScreen(effectBook);
    }

    public boolean isPauseScreen() {
        return false;
    }

}
