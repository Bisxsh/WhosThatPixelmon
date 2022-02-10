package com.bisxsh.whosthatpixelmon.mapItem;

import com.bisxsh.whosthatpixelmon.WhosThatPixelmon;
import com.github.ericliucn.realmap.images.ImageSaveTask;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.LoreData;
import org.spongepowered.api.data.value.mutable.ListValue;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MapMaker {

    private ItemStack hiddenMap, revealedMap;
    private String fileName;
    private String pokemonForm, pokemonName;
    private File chosenSprite;
    private WhosThatPixelmon mainClass;


    public MapMaker() throws IOException {
        this.mainClass = WhosThatPixelmon.getInstance();
        this.generateMapsAndDetails();
    }

    public MapMaker(String fileName) throws IOException {
        this.pokemonName = obtainPokemonName(fileName.substring(0, 3));
        this.pokemonForm = obtainPokemonForm();
        this.fileName = fileName;
        copySpriteToFile(WhosThatPixelmon.getInstance().getSpriteAsset(fileName));
        generateMaps();
    }

    private void generateMapsAndDetails() throws IOException {
        getRandomPokemon();

        String dexNumber = fileName.substring(0, 3);
        obtainPokemonForm();
        obtainPokemonName(dexNumber);

        generateMaps();
    }

    private String obtainPokemonForm() {
        try {
            pokemonForm = fileName.substring(4, chosenSprite.getName().length()-4);
            pokemonForm = pokemonForm.replace("-", " ");
            return pokemonForm;
        } catch (Exception e) {
            //Pokemon does not have a form
        }
        return null;
    }

    private String obtainPokemonName(String dexNumber) {
        //Get pokemon name from dex number
        EnumSpecies enumPokemon = EnumSpecies.getFromDex(Integer.parseInt(dexNumber));
        pokemonName = enumPokemon.getLocalizedName();


        //Remove accents from "Flabebe"
        if (dexNumber.equals("669")) {
            pokemonName = "Flabebe";
        }

        return pokemonName;
    }

    private void getRandomPokemon() throws IOException {
        Asset fileNamesAsset = mainClass.getFileNamesAsset();
        List<String> fileNames = fileNamesAsset.readLines();
        Random rand = new Random();
        String randomFile = fileNames.get(rand.nextInt(fileNames.size()));
        Asset pokemonSpriteAsset = mainClass.getSpriteAsset(randomFile);
        fileName = pokemonSpriteAsset.getFileName();
        copySpriteToFile(pokemonSpriteAsset);
    }

    public void copySpriteToFile(Asset pokemonSpriteAsset) throws IOException {
        File spriteDirectory = new File("config/sprites");
        if (!spriteDirectory.exists()) {
            spriteDirectory.mkdirs();
        }
        pokemonSpriteAsset.copyToDirectory(spriteDirectory.toPath());

        String spritePath = new StringBuilder(spriteDirectory.getPath().toString())
                .append("/").append(fileName).toString();
        chosenSprite = new File(spritePath);
    }


    private void generateMaps() throws IOException {
        //Get colour for map
        Color gray = new Color(160, 160, 160);
        int grayRGB = gray.getRGB();
        //

        //Change pixels on hidden and revealed pixelmon sprites
        BufferedImage hiddenImage = ImageIO.read(chosenSprite);
        BufferedImage revealedImage = ImageIO.read(chosenSprite);
        final int imageHeight = hiddenImage.getHeight();
        final int imageWidth = hiddenImage.getWidth();
        for (int x = 0; x < imageWidth; x++) {
            for (int y = 0; y < imageHeight; y++) {
                Color pixelColor = new Color(hiddenImage.getRGB(x,y));
                if (pixelColor.getRed() == 0 &&
                        pixelColor.getGreen() == 0 &&
                        pixelColor.getBlue() == 0) {
                    hiddenImage.setRGB(x, y, grayRGB);//Sets black pixels to gray
                    revealedImage.setRGB(x, y, grayRGB);//Sets black pixels to gray
                } else {
                    hiddenImage.setRGB(x, y, 4);//Sets black pixels to gray
                }

            }
        }
        //

        //Sets the map texture to the Pixelmon
        ImageSaveTask hiddenImageSaveTask = new ImageSaveTask(hiddenImage);
        ImageSaveTask revealedImageSaveTask = new ImageSaveTask(revealedImage);
        net.minecraft.item.ItemStack hiddenMapIn = hiddenImageSaveTask.getItemStack();
        net.minecraft.item.ItemStack revealedMapIn = revealedImageSaveTask.getItemStack();
        //

        hiddenMap = addMapLore(hiddenMapIn);
        revealedMap = addMapLore(revealedMapIn);

    }

    private ItemStack addMapLore(net.minecraft.item.ItemStack mapIn) {
        //Add lore to the maps
        ItemStack map = ItemStackUtil.fromNative(mapIn);

        final Text loreText = Text.of(TextColors.GOLD, TextStyles.ITALIC, "Who's That Pixelmon?");
        final LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> lore = loreData.lore();
        lore.add(0, loreText);
        loreData.set(lore);

        map.offer(lore);
        return map;
        //
    }

    public static List<Text> getLore() {
        final Text loreText = Text.of(TextColors.GOLD, TextStyles.ITALIC, "Who's That Pixelmon?");
        final LoreData loreData = Sponge.getDataManager().getManipulatorBuilder(LoreData.class).get().create();
        final ListValue<Text> lore = loreData.lore();
        lore.add(0, loreText);
        ItemStack item = ItemStack.builder()
                .itemType(ItemTypes.FILLED_MAP).build();
        item.offer(lore);
        return item.get(Keys.ITEM_LORE).get();
    }

    public void deleteSprite() {
        if (!chosenSprite.delete()) {
            mainClass.getLogger().warn("Sprite file was not deleted");
        }
    }

    public ItemStack getHiddenMap() {
        return hiddenMap;
    }

    public ItemStack getRevealedMap() {
        return revealedMap;
    }

    public String getPokemonForm() {
        return pokemonForm;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public String getDisplayedAnswer() {
        String answer;
        if (pokemonForm != null) {
            answer = new StringBuilder(pokemonName)
                    .append(" (")
                    .append(pokemonForm)
                    .append(")")
                    .toString();
        } else {
            answer = pokemonName;
        }
        return answer;
    }
}
