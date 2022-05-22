/**
 * Introduction to Neural Networks with Java, 2nd Edition
 * Copyright 2008 by Heaton Research, Inc. 
 * http://www.heatonresearch.com/books/java-neural-2/
 * 
 * ISBN13: 978-1-60439-008-7  	 
 * ISBN:   1-60439-008-5
 *   
 * This class is released under the:
 * GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/copyleft/lesser.html
 */
package com.ch13;

import neural.activation.ActivationFunction;
import neural.activation.ActivationSigmoid;
import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;

/**
 * Chapter 13: Bot Programming and Neural Networks
 * 
 * NetworkUtil: Utility class to create neural networks.
 * 
 * @author Jeff Heaton
 * @version 2.1
 */
public class NetworkUtil {

	public static FeedforwardNetwork createNetwork() {
		final ActivationFunction threshold = new ActivationSigmoid();
		final FeedforwardNetwork network = new FeedforwardNetwork();
		network.addLayer(new FeedforwardLayer(threshold, Config.INPUT_SIZE));
		network.addLayer(new FeedforwardLayer(threshold,
				Config.NEURONS_HIDDEN_1));
		if (Config.NEURONS_HIDDEN_2 > 0) {
			network.addLayer(new FeedforwardLayer(threshold,
					Config.NEURONS_HIDDEN_2));
		}
		network.addLayer(new FeedforwardLayer(threshold, Config.OUTPUT_SIZE));
		network.reset();
		return network;
	}
}
