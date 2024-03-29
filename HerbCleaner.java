package scripts;

import org.powerbot.script.Condition;
import org.powerbot.script.Script;
import org.powerbot.script.PollingScript;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;

import java.util.Random;
import java.util.concurrent.Callable;


@Script.Manifest(name="Herb Cleaning", description="test", properties="author=John; topic=999; client=4")
// A crude script for cleaning herbs I had lying around, mostly just testing
public class HerbCleaner extends PollingScript<ClientContext> {

  Random rand = new Random();
  int row = 0;
  int col = 0;

  final static int DIRTY_HERB_ID = 3049;
  final static int CLEAN_HERB_ID = 2998;
  final static int WATER_ID = 227;

  @Override
  public void start() {
    System.out.println("Started");
  }

  @Override
  public void stop() {
    System.out.println("Stopped");
  }

  @Override
  public void poll() {
    assert(!ctx.bank.opened());
    if (col < 4) {
      clean(row, col);
      col += 1;
    } else if ((row == 8) && (col == 4)) {
      System.out.println("Finished Cleaning Inventory");
      bankCleanHerbs();
      withdrawHerbs();
      row = 0;
      col = 0;
    } else {
      row += 1;
      col = 0;
    }
  }

  public void bankCleanHerbs() {
    assert(!ctx.bank.opened());
    ctx.bank.open();
    ctx.bank.deposit(CLEAN_HERB_ID, 28);
  }

  public void withdrawHerbs() {
    assert(ctx.bank.opened());
    if (!ctx.bank.select().id(DIRTY_HERB_ID).isEmpty()) {
      ctx.bank.withdraw(DIRTY_HERB_ID, 28);
    } else {
      ctx.controller.stop();
    }
    boolean succ = ctx.bank.close();
    assert(succ);
  }

  public void clean(int posx, int posy) {
    int waittime = rand.nextInt(50)+50;
    Item dirtyHerb = ctx.inventory.itemAt(posx,posy);
    dirtyHerb.interact("Clean");

    Condition.wait(new Callable<Boolean>() {

      @Override
      public Boolean call() throws Exception {
        // wait until 4x whatever the random number is
        return false;
      }
    }, waittime, 4);
  }

}
