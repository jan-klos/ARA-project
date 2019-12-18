package ara.manet.detection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ara.manet.communication.Emitter;
import ara.util.Message;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

public class NeighborProtocolImpl implements NeighborProtocol, EDProtocol {
	private final static String PAR_PROBE = "probe";
	private final static String PAR_TIMER = "timer";
	private final static String PAR_NEIGHBORHOOD_LISTENER = "neighborhood_listener";
	private final static String PAR_EMITTER = "protocol.emitter";
	
	private final int myPid;
	private int probeTime, timer;
	private final Emitter emitter;
	private final NeighborhoodListener neighborhoodListener;
	private Map<Long, Long> neighborsMap;
	
	public NeighborProtocolImpl(String prefix) {
		String tmp[] = prefix.split("\\.");
		myPid = Configuration.lookupPid(tmp[tmp.length - 1]);
		probeTime = Configuration.getInt(prefix + "." + PAR_PROBE);
		timer = Configuration.getInt(prefix + "." + PAR_TIMER);
		neighborhoodListener = (NeighborhoodListener) Configuration.getInstance(PAR_NEIGHBORHOOD_LISTENER, null);
		emitter = (Emitter) Configuration.getInstance(PAR_EMITTER);
		neighborsMap = new HashMap<Long, Long>();
	}
	
	@Override
	public List<Long> getNeighbors() {
		return new ArrayList<Long>(neighborsMap.keySet());
	}
	
	public void emit(Node host, Message event) {
		emitter.emit(host, event);
	}

	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (pid != myPid)
			throw new RuntimeException("Receive Event for wrong protocol");
		
		if (event instanceof ProbeMessage) {
			long senderId = ((ProbeMessage) event).getIdSrc();
			if (senderId == host.getID()) {
				emit(host, new ProbeMessage(host.getID(), Emitter.ALL, myPid));
			}
			else {
				emit(host, new ProbeAckMessage(host.getID(), senderId, myPid));
				addToNeighbors(senderId);
			}
		} 
		else if (event instanceof ProbeAckMessage) {
			long senderId = ((ProbeAckMessage) event).getIdSrc();
			addToNeighbors(senderId);

		} 
		// remove time-outed neighbors
		neighborsMap.entrySet().removeIf(e -> e.getValue() < CommonState.getTime());
	}
	
	public void addToNeighbors(Long neighborId) {
		neighborsMap.put(neighborId, CommonState.getTime() + timer);
	}

	public Object clone() {
		NeighborProtocolImpl obj = null;
		try {
			obj = (NeighborProtocolImpl) super.clone();
			obj.neighborsMap = new HashMap<Long, Long>(neighborsMap);
			obj.probeTime = probeTime;
			obj.timer = timer;
		} 
		catch (CloneNotSupportedException e) {}
		return obj;
	}
}
