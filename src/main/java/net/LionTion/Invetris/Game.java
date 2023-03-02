package net.LionTion.Invetris;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;

public class Game {

    private int tick = 0;

    private Random random = new Random();

    private Block[] Blocks = new Block[10*20];
    public Block[] Current = new Block[4];
    private BlockType CurrentType = null;
    private int rotation = 0;
    private BlockType Next = null;
    private BlockType Held = null;
    private boolean swapped = false;

    private boolean dropOnNextTick = false;

    private BufferedImage BackgroundImage = null;
    private Color Init = new Color(0, 0, 0, 0);
    private Color background = new Color(20, 20, 20, 255);
    private Color backgroundhighlighter = new Color(186, 186, 186, 255);
    private Color border = new Color(255, 255, 255, 255);
    private Color grid = new Color(60, 60, 60, 255);
    private Color Long = new Color(0, 216, 255, 255);
    private Color Block = new Color(255, 218, 43, 255);
    private Color LL = new Color(0, 48, 201, 255);
    private Color LR = new Color(255, 108, 0, 255);
    private Color T = new Color(115, 0, 255, 255);
    private Color FR = new Color(20, 255, 0, 255);
    private Color FL = new Color(255, 0, 0, 255);

    public Game() {}

    /* public void prepare() {
        MinecraftClient client = MinecraftClient.getInstance();
        Optional<Resource> res = client.getResourceManager().getResource(new Identifier("background", "assets/invetris/background.png"));
        if (res.isPresent()) {
            Resource image = res.get();
            try {
                BackgroundImage = ImageIO.read(image.getInputStream());
            } catch (IOException e) {
                BackgroundImage = null;
                Mod.LOGGER.error("Failed to load background image! Using fallback Background. -> " + e.getMessage());
            }
        }
    } */

    public Block getAt(int x, int y) {
        if (x > 9 || y > 19 || x < 0 || y < 0) return null;
        return Blocks[x+y*10];
    }

    public void setAt(int x, int y, Block block) {
        if (x > 9 || y > 19 || x < 0 || y < 0) return;
        Blocks[x+y*10] = block;
    }
    public void setAt(int x, int y, Color color) {
        if (x > 9 || y > 19 || x < 0 || y < 0) return;
        Blocks[x+y*10] = new Block(color, x, y);
    }

    public void moveAt(int x, int y, int newX, int newY) {
        Block moving = getAt(x, y);
        setAt(x, y, (net.LionTion.Invetris.Block) null);
        setAt(newX, newY, moving);
    }

    public void moveBlocks(int dX, int dY) {
        if (Current[0] == null || Current[1] == null || Current[2] == null || Current[3] == null) {
            spawnNext();
        }
        int move = checkMove(dX, dY);
        if (move == 0) {
            /* Block[] blocks = new Block[Current.length];
            for (int i = 0; i < Current.length; ++i) {
                int x = Current[i].x, y = Current[i].y;
                blocks[i] = getAt(x, y);
                setAt(x, y, (net.LionTion.Invetris.Block) null);
            } */
            /* for (int i = 0; i < Current.length; ++i) {
                Block block = Current[i];
                int x = block.x + dX, y = block.y + dY;
                setAt(x, y, new Block(block.color));
            } */
            for (int i = 0; i < Current.length; ++i) {
                Block block = Current[i];
                Current[i] = new Block(block.color, block.x + dX, block.y + dY);
            }
        } else if (move == 1) {
            setBlocks();
            spawnNext();
        }
        checkLines();
    }

    public void setBlocks() {
        for (int i = 0; i < Current.length; ++i) {
            Block block = Current[i];
            setAt(block.x, block.y, block.color);
        }
    }

    public int checkMove(int dX, int dY) {
        for (int i = 0; i < Current.length; ++i) {
            Block pos = Current[i];
            int newX = pos.x + dX, newY = pos.y + dY;
            if (newY > 19) {
                return 1;
            }
            if (newY < -1 || newX < 0 || newX > 9) {
                return 2;
            }
            Block check = getAt(newX, newY);
            if (check != null) {
                boolean own = false;
                for (int j = 0; j < Current.length; ++j) {
                    Block collisionPos = Current[j];
                    if (collisionPos.x == newX && collisionPos.y == newY) {
                        own = true;
                        break;
                    }
                }
                if (!own) {
                    if (dropOnNextTick) {
                        dropOnNextTick = false;
                        setBlocks();
                        spawnNext();
                    }
                    dropOnNextTick = true;
                    return 3;
                }
            }
        }
        dropOnNextTick = false;
        return 0;
    }

    public void checkLines() {
        for (int y = 19; y > -1; --y) {
            boolean full = true;
            for (int x = 0; x < 10; ++x) {
                Block check = getAt(x, y);
                if (check == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                for (int x = 0; x < 10; ++x) {
                    setAt(x, y, (Block) null);
                }
                for (int i = y-1; i > -1; --i) {
                    for (int x = 0; x < 10; ++x) {
                        moveAt(x, i, x, i+1);
                    }
                }
            }
        }
    }

    public boolean checkLost() {
        for (int i = 0; i < Current.length; ++i) {
            Block pos = Current[i];
            Block now = getAt(pos.x, pos.y);
            if (now != null) {
                return true;
            }
        }
        return false;
    }

    public void clearGame() {
        Blocks = new Block[10*20];
        Held = null;
        swapped = false;
        tick = 0;
    }

    public void restart() {
        Blocks = new Block[10*20];
        Next = null;
        Held = null;
        CurrentType = null;
        Current = new Block[4];
        swapped = false;
        tick = 0;
        rotation = 0;
    }

    public void spawnNext() {
        dropOnNextTick = false;
        swapped = false;
        if (Next == null) {
            Next = BlockType.values()[random.nextInt(BlockType.values().length)];
        }
        if (Next == BlockType.Long) {
            Current = new Block[] { new Block(Long, 5, 0), new Block(Long, 5, 1), new Block(Long, 5, 2), new Block(Long, 5, 3) };
            if (checkLost()) {
                clearGame();
            }
            CurrentType = BlockType.Long;
            rotation = 0;
            /*setAt(5, 0, new Block(Long));
            setAt(5, 1, new Block(Long));
            setAt(5, 2, new Block(Long));
            setAt(5, 3, new Block(Long));*/
        } else if (Next == BlockType.Block) {
            Current = new Block[] { new Block(Block, 4, 0), new Block(Block, 4, 1), new Block(Block, 5, 0), new Block(Block, 5, 1) };
            if (checkLost()) {
                clearGame();
            }
            CurrentType = BlockType.Block;
            rotation = 0;
            /*setAt(4, 0, new Block(Block));
            setAt(4, 1, new Block(Block));
            setAt(5, 0, new Block(Block));
            setAt(5, 1, new Block(Block));*/
        } else if (Next == BlockType.T) {
            Current = new Block[] { new Block(T, 5, 0), new Block(T, 4, 1), new Block(T, 5, 1), new Block(T, 6, 1) };
            if (checkLost()) {
                clearGame();
            }
            CurrentType = BlockType.T;
            rotation = 0;
            /*setAt(5, 0, new Block(T));
            setAt(4, 1, new Block(T));
            setAt(5, 1, new Block(T));
            setAt(6, 1, new Block(T));*/
        } else if (Next == BlockType.LL) {
            Current = new Block[] { new Block(LL, 4, 0), new Block(LL, 4, 1), new Block(LL, 5, 1), new Block(LL, 6, 1) };
            if (checkLost()) {
                clearGame();
            }
            CurrentType = BlockType.LL;
            rotation = 0;
            /*setAt(4, 0, new Block(LL));
            setAt(4, 1, new Block(LL));
            setAt(5, 1, new Block(LL));
            setAt(6, 1, new Block(LL));*/
        } else if (Next == BlockType.LR) {
            Current = new Block[] { new Block(LR, 6, 0), new Block(LR, 4, 1), new Block(LR, 5, 1), new Block(LR, 6, 1) };
            if (checkLost()) {
                clearGame();
            }
            CurrentType = BlockType.LR;
            rotation = 0;
            /*setAt(6, 0, new Block(LR));
            setAt(4, 1, new Block(LR));
            setAt(5, 1, new Block(LR));
            setAt(6, 1, new Block(LR));*/
        } else if (Next == BlockType.FL) {
            Current = new Block[] { new Block(FL, 4, 0), new Block(FL, 5, 0), new Block(FL, 5, 1), new Block(FL, 6, 1) };
            if (checkLost()) {
                clearGame();
            }
            CurrentType = BlockType.FL;
            rotation = 0;
            /*setAt(4, 0, new Block(FL));
            setAt(5, 0, new Block(FL));
            setAt(5, 1, new Block(FL));
            setAt(6, 1, new Block(FL));*/
        } else if (Next == BlockType.FR) {
            Current = new Block[] { new Block(FR, 4, 1), new Block(FR, 5, 1), new Block(FR, 5, 0), new Block(FR, 6, 0) };
            if (checkLost()) {
                clearGame();
            }
            CurrentType = BlockType.FR;
            rotation = 0;
            /*setAt(4, 1, new Block(FR));
            setAt(5, 1, new Block(FR));
            setAt(5, 0, new Block(FR));
            setAt(6, 0, new Block(FR));*/
        }
        Next = BlockType.values()[random.nextInt(BlockType.values().length)];
    }

    public void spawnHeld() {
        if (Held == null) {
            dropOnNextTick = false;
            Held = CurrentType;
            if (Next == BlockType.Long) {
                Current = new Block[] { new Block(Long, 5, 0), new Block(Long, 5, 1), new Block(Long, 5, 2), new Block(Long, 5, 3) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.Long;
                rotation = 0;
            } else if (Next == BlockType.Block) {
                Current = new Block[] { new Block(Block, 4, 0), new Block(Block, 4, 1), new Block(Block, 5, 0), new Block(Block, 5, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.Block;
                rotation = 0;
            } else if (Next == BlockType.T) {
                Current = new Block[] { new Block(T, 5, 0), new Block(T, 4, 1), new Block(T, 5, 1), new Block(T, 6, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.T;
                rotation = 0;
            } else if (Next == BlockType.LL) {
                Current = new Block[] { new Block(LL, 4, 0), new Block(LL, 4, 1), new Block(LL, 5, 1), new Block(LL, 6, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.LL;
                rotation = 0;
            } else if (Next == BlockType.LR) {
                Current = new Block[] { new Block(LR, 6, 0), new Block(LR, 4, 1), new Block(LR, 5, 1), new Block(LR, 6, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.LR;
                rotation = 0;
            } else if (Next == BlockType.FL) {
                Current = new Block[] { new Block(FL, 4, 0), new Block(FL, 5, 0), new Block(FL, 5, 1), new Block(FL, 6, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.FL;
                rotation = 0;
            } else if (Next == BlockType.FR) {
                Current = new Block[] { new Block(FR, 4, 1), new Block(FR, 5, 1), new Block(FR, 5, 0), new Block(FR, 6, 0) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.FR;
                rotation = 0;
            }
            Next = BlockType.values()[random.nextInt(BlockType.values().length)];
        } else if (!swapped) {
            swapped = true;
            BlockType cur = CurrentType;
            if (Held == BlockType.Long) {
                Current = new Block[] { new Block(Long, 5, 0), new Block(Long, 5, 1), new Block(Long, 5, 2), new Block(Long, 5, 3) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.Long;
                rotation = 0;
            } else if (Held == BlockType.Block) {
                Current = new Block[] { new Block(Block, 4, 0), new Block(Block, 4, 1), new Block(Block, 5, 0), new Block(Block, 5, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.Block;
                rotation = 0;
            } else if (Held == BlockType.T) {
                Current = new Block[] { new Block(T, 5, 0), new Block(T, 4, 1), new Block(T, 5, 1), new Block(T, 6, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.T;
                rotation = 0;
            } else if (Held == BlockType.LL) {
                Current = new Block[] { new Block(LL, 4, 0), new Block(LL, 4, 1), new Block(LL, 5, 1), new Block(LL, 6, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.LL;
                rotation = 0;
            } else if (Held == BlockType.LR) {
                Current = new Block[] { new Block(LR, 6, 0), new Block(LR, 4, 1), new Block(LR, 5, 1), new Block(LR, 6, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.LR;
                rotation = 0;
            } else if (Held == BlockType.FL) {
                Current = new Block[] { new Block(FL, 4, 0), new Block(FL, 5, 0), new Block(FL, 5, 1), new Block(FL, 6, 1) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.FL;
                rotation = 0;
            } else if (Held == BlockType.FR) {
                Current = new Block[] { new Block(FR, 4, 1), new Block(FR, 5, 1), new Block(FR, 5, 0), new Block(FR, 6, 0) };
                if (checkLost()) {
                    clearGame();
                }
                CurrentType = BlockType.FR;
                rotation = 0;
            }
            Held = cur;
        }

    }

    synchronized public BufferedImage render() {
        BufferedImage frame = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) frame.createGraphics();
        g.setColor(Init);
        g.fillRect(0, 0, 128, 128);
        /*if (BackgroundImage == null) {

        } else {
            g.drawImage(BackgroundImage, 0, 0, null);
        } */
        g.setColor(backgroundhighlighter);
        g.fillRoundRect(0, 0, 128, 128, 21, 21);
        g.setColor(background);
        g.fillRoundRect(1, 1, 126, 126, 21, 21);
        g.setColor(grid);
        for (int i = 45; i < 90; i += 5) {
            g.drawLine(i, 16, i, 116);
        }
        for (int i = 21; i < 116; i += 5) {
            g.drawLine(40, i, 90, i);
        }
        g.setColor(border);
        g.drawRect(10, 10, 25, 25);
        g.drawRect(94, 10, 25, 25);
        g.drawRect(40, 16, 51, 101);
        for (int y = 0; y < 20; ++y) {
            for (int x = 0; x < 10; ++x) {
                Block block = getAt(x, y);
                if (block != null) {
                    g.setColor(block.color);
                    g.fillRect(41+x*5, 17+y*5, 5, 5);
                }
            }
        }
        for (int i = 0; i < Current.length; ++i) {
            Block block = Current[i];
            if (block != null) {
                g.setColor(block.color);
                g.fillRect(41+block.x*5, 17+block.y*5, 5, 5);
            }
        }
        if (Next != null) {
            if (Next == BlockType.Long) {
                g.setColor(Long);
                g.fillRect(105, 13, 5, 20);
            } else if (Next == BlockType.Block) {
                g.setColor(Block);
                g.fillRect(103, 18, 10, 10);
            } else if (Next == BlockType.T) {
                g.setColor(T);
                g.fillRect(104, 17, 5, 5);
                g.fillRect(99, 22, 15, 5);
            } else if (Next == BlockType.LL) {
                g.setColor(LL);
                g.fillRect(99, 17, 5, 5);
                g.fillRect(99, 22, 15, 5);
            } else if (Next == BlockType.LR) {
                g.setColor(LR);
                g.fillRect(109, 17, 5, 5);
                g.fillRect(99, 22, 15, 5);
            } else if (Next == BlockType.FL) {
                g.setColor(FL);
                g.fillRect(104, 22, 10, 5);
                g.fillRect(99, 17, 10, 5);
            } else if (Next == BlockType.FR) {
                g.setColor(FR);
                g.fillRect(99, 22, 10, 5);
                g.fillRect(104, 17, 10, 5);
            }
        }
        if (Held != null) {
            if (Held == BlockType.Long) {
                g.setColor(Long);
                g.fillRect(19, 13, 5, 20);
            } else if (Held == BlockType.Block) {
                g.setColor(Block);
                g.fillRect(18, 18, 10, 10);
            } else if (Held == BlockType.T) {
                g.setColor(T);
                g.fillRect(20, 17, 5, 5);
                g.fillRect(15, 22, 15, 5);
            } else if (Held == BlockType.LL) {
                g.setColor(LL);
                g.fillRect(15, 17, 5, 5);
                g.fillRect(15, 22, 15, 5);
            } else if (Held == BlockType.LR) {
                g.setColor(LR);
                g.fillRect(25, 17, 5, 5);
                g.fillRect(15, 22, 15, 5);
            } else if (Held == BlockType.FL) {
                g.setColor(FL);
                g.fillRect(20, 22, 10, 5);
                g.fillRect(15, 17, 10, 5);
            } else if (Held == BlockType.FR) {
                g.setColor(FR);
                g.fillRect(15, 22, 10, 5);
                g.fillRect(20, 17, 10, 5);
            }
        }
        g.dispose();
        return frame;
    }

    public void tick() {
        ++tick;
        if (tick > 19) {
            tick = 0;
            cycle();
        }
    }

    public void cycle() {
        moveBlocks(0, 1);
        checkLines();
    }

    public boolean checkRotation(Block[] newRotation) {
        for (int i = 0; i < newRotation.length; ++i) {
            Block block = newRotation[i];
            if (block.x < 0 || block.y < 0 || block.x > 9 ||block.y > 19) {
                return false;
            }
            if (getAt(block.x, block.y) != null) {
                return false;
            }
        }
        return true;
    }

    public void rotateCurrent() {
        if (CurrentType == BlockType.Long) {
            if (rotation == 0) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x - 2, y);
                newCurrent[1] = new Block(anchor.color, x - 1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x + 1, y);
                if (checkRotation(newCurrent)) {
                    rotation = 1;
                    Current = newCurrent;
                }
            } else if (rotation == 1) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y - 2);
                newCurrent[1] = new Block(anchor.color, x, y - 1);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 0;
                    Current = newCurrent;
                }
            }
        } else if (CurrentType == BlockType.T) {
            if (rotation == 0) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y - 1);
                newCurrent[1] = new Block(anchor.color, x, y);
                newCurrent[2] = new Block(anchor.color, x + 1, y);
                newCurrent[3] = new Block(anchor.color, x, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 1;
                    Current = newCurrent;
                }
            } else if (rotation == 1) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[1];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y);
                newCurrent[1] = new Block(anchor.color, x - 1, y);
                newCurrent[2] = new Block(anchor.color, x + 1, y);
                newCurrent[3] = new Block(anchor.color, x, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 2;
                    Current = newCurrent;
                }
            } else if (rotation == 2) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[0];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y - 1);
                newCurrent[1] = new Block(anchor.color, x - 1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 3;
                    Current = newCurrent;
                }
            } else if (rotation == 3) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y - 1);
                newCurrent[1] = new Block(anchor.color, x - 1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x + 1, y);
                if (checkRotation(newCurrent)) {
                    rotation = 0;
                    Current = newCurrent;
                }
            }
        } else if (CurrentType == BlockType.LR) {
            if (rotation == 0) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y - 1);
                newCurrent[1] = new Block(anchor.color, x, y);
                newCurrent[2] = new Block(anchor.color, x, y + 1);
                newCurrent[3] = new Block(anchor.color, x + 1, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 1;
                    Current = newCurrent;
                }
            } else if (rotation == 1) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[1];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x - 1, y + 1);
                newCurrent[1] = new Block(anchor.color, x - 1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x + 1, y);
                if (checkRotation(newCurrent)) {
                    rotation = 2;
                    Current = newCurrent;
                }
            } else if (rotation == 2) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x - 1, y - 1);
                newCurrent[1] = new Block(anchor.color, x, y - 1);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 3;
                    Current = newCurrent;
                }
            } else if (rotation == 3) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x - 1, y);
                newCurrent[1] = new Block(anchor.color, x + 1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x + 1, y - 1);
                if (checkRotation(newCurrent)) {
                    rotation = 0;
                    Current = newCurrent;
                }
            }
        } else if (CurrentType == BlockType.LL) {
            if (rotation == 0) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x + 1, y - 1);
                newCurrent[1] = new Block(anchor.color, x, y - 1);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 1;
                    Current = newCurrent;
                }
            } else if (rotation == 1) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x - 1, y);
                newCurrent[1] = new Block(anchor.color, x, y);
                newCurrent[2] = new Block(anchor.color, x + 1, y);
                newCurrent[3] = new Block(anchor.color, x + 1, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 2;
                    Current = newCurrent;
                }
            } else if (rotation == 2) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[1];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y - 1);
                newCurrent[1] = new Block(anchor.color, x, y);
                newCurrent[2] = new Block(anchor.color, x, y + 1);
                newCurrent[3] = new Block(anchor.color, x - 1, y + 1);
                if (checkRotation(newCurrent)) {
                    rotation = 3;
                    Current = newCurrent;
                }
            } else if (rotation == 3) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[1];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x - 1, y - 1);
                newCurrent[1] = new Block(anchor.color, x - 1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x + 1, y);
                if (checkRotation(newCurrent)) {
                    rotation = 0;
                    Current = newCurrent;
                }
            }
        } else if (CurrentType == BlockType.FR) {
            if (rotation == 0) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[1];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y-1);
                newCurrent[1] = new Block(anchor.color, x+1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x+1, y+1);
                if (checkRotation(newCurrent)) {
                    rotation = 1;
                    Current = newCurrent;
                }
            } else if (rotation == 1) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x+1, y);
                newCurrent[1] = new Block(anchor.color, x, y+1);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x-1, y+1);
                if (checkRotation(newCurrent)) {
                    rotation = 2;
                    Current = newCurrent;
                }
            } else if (rotation == 2) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x-1, y-1);
                newCurrent[1] = new Block(anchor.color, x-1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x, y+1);
                if (checkRotation(newCurrent)) {
                    rotation = 3;
                    Current = newCurrent;
                }
            } else if (rotation == 3) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x-1, y);
                newCurrent[1] = new Block(anchor.color, x, y);
                newCurrent[2] = new Block(anchor.color, x, y-1);
                newCurrent[3] = new Block(anchor.color, x+1, y-1);
                if (checkRotation(newCurrent)) {
                    rotation = 0;
                    Current = newCurrent;
                }
            }
        } else if (CurrentType == BlockType.FL) {
            if (rotation == 0) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x+1, y-1);
                newCurrent[1] = new Block(anchor.color, x+1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x, y+1);
                if (checkRotation(newCurrent)) {
                    rotation = 1;
                    Current = newCurrent;
                }
            } else if (rotation == 1) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x-1, y);
                newCurrent[1] = new Block(anchor.color, x, y);
                newCurrent[2] = new Block(anchor.color, x, y+1);
                newCurrent[3] = new Block(anchor.color, x+1, y+1);
                if (checkRotation(newCurrent)) {
                    rotation = 2;
                    Current = newCurrent;
                }
            } else if (rotation == 2) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[1];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x, y-1);
                newCurrent[1] = new Block(anchor.color, x-1, y);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x-1, y+1);
                if (checkRotation(newCurrent)) {
                    rotation = 3;
                    Current = newCurrent;
                }
            } else if (rotation == 3) {
                Block[] newCurrent = new Block[4];
                Block anchor = Current[2];
                int y = anchor.y, x = anchor.x;
                newCurrent[0] = new Block(anchor.color, x-1, y-1);
                newCurrent[1] = new Block(anchor.color, x, y-1);
                newCurrent[2] = new Block(anchor.color, x, y);
                newCurrent[3] = new Block(anchor.color, x+1, y);
                if (checkRotation(newCurrent)) {
                    rotation = 0;
                    Current = newCurrent;
                }
            }
        }
    }

}
