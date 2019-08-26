package other;

import objects.*;

import java.awt.*;

/**
 * Created by aka_npou on 29.12.2018.
 */
public class Draw {
    private Canvas canvas;

    private Graphics g;
    private int offsetX;
    private int offsetY;
    public boolean clear;
    private int scale;

    private int width;
    private int depth;
    private boolean invert;

    Color[] t_colors = new Color[6];
    Color[] l_colors = new Color[6];



    public Draw(int x, int y, int offsetX, int offsetY, int scale, boolean invert) {
        if (!Game.DEBUG)
            return;

        canvas = new Canvas((int)(x/scale+offsetX*4), (int)(y/scale+offsetY*4));
        g = canvas.getGraphics();
        width=y;
        depth=x;
        this.scale=scale;
        this.invert=invert;

        this.offsetX = offsetX;
        this.offsetY = offsetY;

        t_colors[0] = new Color(90, 159, 153, 200);
        t_colors[1] = new Color(216, 27, 96, 200);
        t_colors[2] = new Color(96, 125, 139, 200);
        t_colors[3] = new Color(245, 124, 0, 200);
        t_colors[4] = new Color(92, 107, 192, 200);
        t_colors[5] = new Color(141, 110, 99, 200);

        l_colors[0] = new Color(90, 159, 153,100);
        l_colors[1] = new Color(216, 27, 96, 100);
        l_colors[2] = new Color(96, 125, 139,100);
        l_colors[3] = new Color(245, 124, 0, 100);
        l_colors[4] = new Color(92, 107, 192,100);
        l_colors[5] = new Color(141, 110, 99,100);

        drawPole();

    }


    void clear() {
        if (!Game.DEBUG)
            return;

        g.clearRect(0, 0, (int)(depth/scale+offsetX), (int)(width/scale+offsetY));
        g.setColor(Color.BLACK);

    }

    public void drawCircle(double x, double y, double r) {
        if (!check())
            return;

        g.setColor(Color.black);
        g.drawOval((int)(depth/2*scale+offsetX+(x-r)*scale),(int)(width/2*scale+offsetY+(y-r)*scale),(int)(r*2*scale),(int)(r*2*scale));
    }

    public void drawRect(int x1, int y1, int w, int h, Color color) {
        if (!check())
            return;

        //g.clearRect(0,0,512,512);
        g.setColor(color);
        g.fillRect(offsetX+x1,offsetY+y1,w,h);
    }

    public void drawLine(double x1, double y1,double x2, double y2) {
        if (!check())
            return;

        //g.clearRect(0,0,512,512);
        //g.setColor(Color.red);
        g.drawLine((int)(depth/2*scale+offsetX+x1*scale),(int)(width/2*scale+offsetY+y1*scale),(int)(depth/2*scale+offsetX+x2*scale),(int)(width/2*scale+offsetY+y2*scale));
    }

    boolean check() {
        if (!Game.DEBUG)
            return false;

        //g.clearRect(0,0,512,512);
        if (!clear) {
            clear = true;
            clear();
            drawPole();
        }

        return true;
    }

    void drawPole() {
        g.setColor(Color.black);

        int x = 0,y = 0;

        while (true) {
            if (y>Game.Y_CELLS_COUNT)
                break;

            g.drawLine(0+offsetX,y*Game.WIDTH / scale+offsetY,Game.X_CELLS_COUNT*Game.WIDTH / scale+offsetX,y*Game.WIDTH / scale+offsetY);

            y+=1;

        }

        while (true) {
            if (x>Game.X_CELLS_COUNT)
                break;

            g.drawLine(x*Game.WIDTH / scale+offsetX,0+offsetY,x*Game.WIDTH / scale+offsetX,Game.Y_CELLS_COUNT*Game.WIDTH / scale+offsetY);

            x+=1;

        }

        g.setColor(Color.red);

        y = 0;

        while (true) {
            if (y>Game.Y_CELLS_COUNT)
                break;

            g.drawLine(0+offsetX,(Game.Y_CELLS_COUNT-1-y)*Game.WIDTH / scale+offsetY,Game.X_CELLS_COUNT*Game.WIDTH / scale+offsetX,(Game.Y_CELLS_COUNT-1-y)*Game.WIDTH / scale+offsetY);

            y+=5;

        }

        x = 0;

        while (true) {
            if (x>Game.X_CELLS_COUNT)
                break;

            g.drawLine((Game.X_CELLS_COUNT-1-x)*Game.WIDTH / scale+offsetX,0+offsetY,(Game.X_CELLS_COUNT-1-x)*Game.WIDTH / scale+offsetX,Game.Y_CELLS_COUNT*Game.WIDTH / scale+offsetY);

            x+=5;

        }

    }

    public void drawPlayer(Player player, int id) {

        for (objects.Point t:player.territory) {
            drawRect((t.x-Game.WIDTH2)/scale, ((Game.Y_CELLS_COUNT-1)*Game.WIDTH - (t.y-Game.WIDTH2))/scale, Game.WIDTH/scale, Game.WIDTH/scale, t_colors[id]);
        }

        for (objects.Point l:player.lines) {
            drawRect((l.x-Game.WIDTH2)/scale, ((Game.Y_CELLS_COUNT-1)*Game.WIDTH - (l.y-Game.WIDTH2))/scale, Game.WIDTH/scale, Game.WIDTH/scale, l_colors[id]);
        }

        drawRect((player.position.x-Game.WIDTH2+5)/scale, ((Game.Y_CELLS_COUNT-1)*Game.WIDTH -(player.position.y-Game.WIDTH2-5))/scale, (Game.WIDTH-10)/scale, (Game.WIDTH-10)/scale, t_colors[id]);

    }
}

