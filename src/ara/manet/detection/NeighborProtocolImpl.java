package ara.manet.detection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ara.manet.communication.Emitter;
import ara.util.ProbeMessage;
import peersim.config.Configuration;
import peersim.core.Node;

public class NeighborProtocolImpl implements NeighborProtocol {
	private final static String PAR_PROBE = "probe";
	private final static String PAR_TIMER = "timer";
	private final static String PAR_NEIGHBORHOOD_LISTENER = "neighborhood_listener";
	private final static String PAR_EMITTER = "protocol.emitter";
	
	private final int myPid, probe, timer;
	private final Emitter emitter;
	private final NeighborhoodListener neighborhoodListener;
	private Map<Long, Integer> neighbors;
	private final long ALL = (long) -2;
	
	public NeighborProtocolImpl(String prefix) {
		String tmp[] = prefix.split("\\.");
		myPid = Configuration.lookupPid(tmp[tmp.length - 1]);
		probe = Configuration.getInt(prefix + "." + PAR_PROBE);
		timer = Configuration.getInt(prefix + "." + PAR_TIMER);
		neighborhoodListener = (NeighborhoodListener) Configuration.getInstance(PAR_NEIGHBORHOOD_LISTENER, null);
		emitter = (Emitter) Configuration.getInstance(PAR_EMITTER);
		neighbors = new HashMap<Long, Integer>();
	}
	
	@Override
	public List<Long> getNeighbors() {
		List<Long> neighborsIdList = new ArrayList<Long>();
		for (Long neighborId:neighbors.keySet()) {
			neighborsIdList.add(neighborId);
		}
		return neighborsIdList;
	}
	
	public void heartbeat(Node sender){
		long senderId = sender.getID();
		ProbeMessage msg = new ProbeMessage(senderId, ALL, myPid, timer, probe);
		emitter.emit(sender, msg);
	}

	public Object clone() {
		NeighborProtocol obj = null;
		
		try {
			obj = (NeighborProtocolImpl) super.clone();
		} 
		catch (CloneNotSupportedException e) {}
		return obj;
	}
}
