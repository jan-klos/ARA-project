package ara.manet.detection;

import peersim.core.Node;

public class NeighborhoodListenerImpl implements NeighborhoodListener {
	
	public void newNeighborDetected(Node host, long id_new_neighbor) {
	}

	public void lostNeighborDetected(Node host, long id_lost_neighbor) {
	}
	
	public Object clone() {
		NeighborhoodListener obj = null;
		
		try {
			obj = (NeighborhoodListenerImpl) super.clone();
		} 
		catch (CloneNotSupportedException e) {}
		return obj;
	}
}
