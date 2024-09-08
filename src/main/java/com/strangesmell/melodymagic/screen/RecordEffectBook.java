package com.strangesmell.melodymagic.screen;

import com.strangesmell.melodymagic.api.RecordUtil;
import com.strangesmell.melodymagic.api.SoundEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.KEY2EFFECT;

public class RecordEffectBook extends Screen {
    private static final Component SELECT = Component.translatable("melodymagic.recode_select");
    private static final Component NEXT = Component.translatable("melodymagic.recode_next");
    private static final Component PREVIOUS = Component.translatable("melodymagic.recode_previous");
    private static final Component SOUNDSCREEN = Component.translatable("melodymagic.sound_screen");
    protected EditBox nameEdit;
    protected int page;
    protected int searchPage;
    protected String searchName;

    protected Button openSoundButton;
    protected Button nextButton;
    protected Button previousButton;
    protected List<String> effectList= new ArrayList<>();
    protected List<String> tranList= new ArrayList<>();
    protected Player player;

    public RecordEffectBook(Player  player_ ) {
        super(Component.empty());
        page=0;
        searchPage=0;
        player = player_;
        RecordUtil.loadEffectKinds(player_,effectList);
        for(int i=0;i<effectList.size();i++){
            tranList.add(KEY2EFFECT.get(effectList.get(i)).name(null,null,null,null));
        }
    }
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        this.nameEdit.render( graphics,  mouseX,  mouseY,  partialTick);
        this.openSoundButton.render( graphics,  mouseX,  mouseY,  partialTick);

        graphics.drawCenteredString(this.font, SELECT, 20, 15, 16777215);
        String name = this.nameEdit.getValue();

        if(name.equals("")){
            int size = effectList.size();

            if(page*10+10<size) nextButton.render( graphics,  mouseX,  mouseY,  partialTick);
            if(page>0) previousButton.render(graphics,  mouseX,  mouseY,  partialTick);

            for(int i = 0; i+page*10<size && i<10;i++){
                SoundEffect soundEffect = KEY2EFFECT.get(effectList.get(page*10+i));
                rendIcon(graphics,  30,  this.height / 8 + 20 * i +3,  partialTick, soundEffect);
                graphics.drawString(this.font,Component.translatable(tranList.get(i)), 60, this.height / 8 + 20 * i+3 , 16777215 );
            }
            graphics.drawString(this.font,tranList.size() +" / "+ KEY2EFFECT.size(), (this.width- this.font.width(tranList.size() +" / "+ KEY2EFFECT.size()))/2, this.height -80, 16777215 );


        }else{
            List<String> values= new ArrayList<>();
            for (String s : tranList) if (s.toString().contains(name)) values.add(s.toString());

            int size = values.size();
            if(searchPage*10+10<size) nextButton.render( graphics,  mouseX,  mouseY,  partialTick);
            if(searchPage>0) previousButton.render(graphics,  mouseX,  mouseY,  partialTick);

            for(int i = 0; i+page*10<size&&i<10;i++){

                SoundEffect soundEffect = KEY2EFFECT.get(values.get(page*10+i));
                rendIcon(graphics,  30,  this.height / 8 + 20 * i +3,  partialTick, soundEffect);

                graphics.drawString(this.font, Component.translatable(soundEffect.name(null,null,null,null)), 60, this.height / 8 + 20 * i +3, 16777215 );
            }
        }

    }

    @Override
    protected void init() {
        super.init();
        page=0;

        openSoundButton = this.addRenderableWidget(Button.builder(SOUNDSCREEN, (a) -> {
            this.openSoundButton();
        }).bounds(this.width/2 -15,  this.height -60, 30, 20).build());


        nextButton = this.addRenderableWidget(Button.builder(NEXT, (p_97691_) -> {
            this.nextDone();
        }).bounds(this.width-60,  this.height -60, 30, 20).build());

        previousButton = this.addRenderableWidget( Button.builder(PREVIOUS, (p_97691_) -> {
            this.previousDone();
        }).bounds(30 , this.height -60, 30, 20).build());


        this.nameEdit = new EditBox(this.font, this.width / 2 - 150, 10, 300, 15, Component.translatable("mcspeed.command"));
        this.nameEdit.setMaxLength(32500);
        this.addRenderableWidget(nameEdit);
    }

    protected void rendIcon(GuiGraphics graphics, int x, int y, float partialTick, SoundEffect soundEffect) {
        graphics.blit(soundEffect.getRes(),x,y,0,0,soundEffect.getWeight(),soundEffect.getHeight(),soundEffect.getWeight(),soundEffect.getHeight());
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

    }

    protected void openSoundButton(){
        RecordSoundBook effectBook = new RecordSoundBook(player);
        Minecraft.getInstance().setScreen(effectBook);
    }


    public boolean isPauseScreen() {
        return false;
    }

}
