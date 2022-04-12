package com.ch7.tsp;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Chapter 7:Training using Simulated Annealing
 *
 * WorldMap: Holds the map for the traveling salesman problem.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class WorldMap extends JPanel {

    /**
     * Serial id for this class.
     */
    private static final long serialVersionUID = 5806019562416892419L;
    /**
     * The TravelingSalesman object that owns this object.
     */
    protected AnnealTravelingSalesman owner;

    /**
     * Constructor.
     *
     * @param owner
     *            The TravelingSalesman object that owns this object.
     */
    WorldMap(final AnnealTravelingSalesman owner) {
        this.owner = owner;
    }

    /**
     * Update the graphical display of the map.
     *
     * @param g
     *            The graphics object to use.
     */
    @Override
    public void paint(final Graphics g) {
        update(g);
    }

    /**
     * Update the graphical display of the map.
     *
     * @param g
     *            The graphics object to use.
     */
    @Override
    public void update(final Graphics g) {
        final int width = getBounds().width;
        final int height = getBounds().height;

        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        if (!this.owner.started) {
            return;
        }

        g.setColor(Color.red);
        for (int i = 0; i < AnnealTravelingSalesman.CITY_COUNT; i++) {
            final int xpos = this.owner.cities[i].getx();
            final int ypos = this.owner.cities[i].gety();
            g.fillOval(xpos - 5, ypos - 5, 10, 10);
        }

        final Integer[] path = this.owner.getAnneal().getArray();

        g.setColor(Color.white);
        for (int i = 0; i < path.length - 1; i++) {
            final int icity = path[i];
            final int icity2 = path[i + 1];

            g.drawLine(this.owner.cities[icity].getx(),
                    this.owner.cities[icity].gety(), this.owner.cities[icity2]
                            .getx(), this.owner.cities[icity2].gety());

        }
    }

}
