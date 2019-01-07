 package sneckomod.actions;

 import com.badlogic.gdx.graphics.Color;
 import com.megacrit.cardcrawl.actions.AbstractGameAction;
 import com.megacrit.cardcrawl.actions.common.ExhaustSpecificCardAction;
 import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
 import com.megacrit.cardcrawl.cards.AbstractCard;
 import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
 import com.megacrit.cardcrawl.cards.colorless.Madness;
 import com.megacrit.cardcrawl.characters.AbstractPlayer;
 import com.megacrit.cardcrawl.core.AbstractCreature;
 import com.megacrit.cardcrawl.core.CardCrawlGame;
 import com.megacrit.cardcrawl.core.Settings;
 import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
 import com.megacrit.cardcrawl.helpers.CardLibrary;
 import com.megacrit.cardcrawl.localization.UIStrings;
 import sneckomod.SneckoMod;

 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.Map.Entry;


 public class SingleExchangeAction
   extends AbstractGameAction
 {
   public SingleExchangeAction(AbstractCreature target, AbstractCreature source, int amount, boolean isRandom, boolean anyNumber, boolean canPickZero)
   {
     this.canPickZero = false;
     this.anyNumber = anyNumber;
     this.canPickZero = canPickZero;
     this.p = ((AbstractPlayer)target);
     this.isRandom = isRandom;
     setValues(target, source, amount);
     this.duration = Settings.ACTION_DUR_FAST;
     this.actionType = ActionType.EXHAUST;
   }

   public void update() {
     if (this.duration == Settings.ACTION_DUR_FAST) {
       if (this.p.hand.size() == 0) {
         this.isDone = true;
         return;
       }


       if ((!this.anyNumber) && (this.p.hand.size() <= this.amount)) {
         this.amount = this.p.hand.size();
         numExhausted = this.amount;
         int i2 = this.p.hand.size();

         for (int i = 0; i < i2; i++) {
           AbstractCard c = this.p.hand.getTopCard();
           this.p.hand.moveToExhaustPile(c);
           dissolveEffect(c);
         }


         CardCrawlGame.dungeon.checkForPactAchievement();
         return;
       }

       if (!this.isRandom) {
         numExhausted = this.amount;
         AbstractDungeon.handCardSelectScreen.open(TEXT[0], this.amount, this.anyNumber, this.canPickZero);
         tickDuration();
         return;
       }

       for (int i = 0; i < this.amount; i++) {
         AbstractCard c = this.p.hand.getRandomCard(AbstractDungeon.cardRandomRng);
         this.p.hand.moveToExhaustPile(c);
         dissolveEffect(c);
       }


       CardCrawlGame.dungeon.checkForPactAchievement();
     }

     if (!AbstractDungeon.handCardSelectScreen.wereCardsRetrieved) {
       Iterator var4 = AbstractDungeon.handCardSelectScreen.selectedCards.group.iterator();

       while (var4.hasNext()) {
         AbstractCard c = (AbstractCard)var4.next();
         this.p.hand.moveToExhaustPile(c);
         dissolveEffect(c);
       }

       CardCrawlGame.dungeon.checkForPactAchievement();
       AbstractDungeon.handCardSelectScreen.wereCardsRetrieved = true;
     }

     tickDuration();
   }

   public void dissolveEffect(AbstractCard c2)
   {



     ArrayList<String> tmp = new ArrayList();
     Iterator var3 = CardLibrary.cards.entrySet().iterator();

     while (var3.hasNext()) {
       Entry<String, AbstractCard> c = (Entry)var3.next();
       if (((AbstractCard)c.getValue()).type == c2.type) {
         tmp.add(c.getKey());
       }
     }


         AbstractCard cNew;

       if (tmp.size() > 0) {
         cNew = CardLibrary.cards.get(tmp.get(AbstractDungeon.cardRng.random(0, tmp.size() - 1)));
       } else {
         cNew = new Madness();
       }

       if ((cNew.cost >= 0) && (!cNew.hasTag(SneckoMod.SNEKPROOF))) {
         int newCost = AbstractDungeon.cardRandomRng.random(3);
         if (cNew.cost != newCost) {
           cNew.cost = newCost;
           cNew.costForTurn = cNew.cost;
           cNew.isCostModified = true;
           cNew.superFlash(Color.PURPLE.cpy());
         }


       AbstractDungeon.actionManager.addToBottom(new MakeTempCardInHandAction(cNew));
     }
   }











   private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("ExhaustAction");
   public static final String[] TEXT = uiStrings.TEXT;
   private AbstractPlayer p;
   private boolean isRandom;
   private boolean anyNumber;
   private boolean canPickZero;
   private int block;
   private int extraCards;
   public static int numExhausted;
 }


