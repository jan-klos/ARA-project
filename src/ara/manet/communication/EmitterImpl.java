package ara.manet.communication;

import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.config.Configuration;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import ara.manet.positioning.Position;
import ara.manet.positioning.PositionProtocolImpl;
import ara.util.Message;

public class EmitterImpl implements Emitter {
	public static final String EMIT_EVENT = "EMITEVENT";
	
	private static final String PAR_LATENCY = "latency";
	private static final String PAR_VARIANCE_ACTIVE = "variance_active";
	private static final String PAR_SCOPE = "scope";
	private static final String PAR_POSITION_PROTOCOL = "position_protocol";
	
	private final int myPid, latency, scope, positionPid;
	private final boolean varianceActive;
	
	public EmitterImpl(String prefix) {
		String tmp[] = prefix.split("\\.");
		myPid = Configuration.lookupPid(tmp[tmp.length - 1]);
		latency = Configuration.getInt(prefix + "." + PAR_LATENCY);
		varianceActive = Configuration.getBoolean(prefix + "." + PAR_VARIANCE_ACTIVE);
		scope = Configuration.getInt(prefix + "." + PAR_SCOPE);
		positionPid = Configuration.lookupPid(PAR_POSITION_PROTOCOL);
	}
	
	@Override
	public void processEvent(Node host, int pid, Object event) {
		if (pid != myPid) {
			throw new RuntimeException("Receive Event for wrong protocol");
		}
		if (event instanceof Message) {
			Message msg = (Message) event;
			int msgPid = msg.getPid();
			if (msg.getIdDest() == host.getID() || msg.getIdDest() == Emitter.ALL) {
				((EDProtocol) host.getProtocol(msgPid)).processEvent(host, msgPid, msg);
			}
		}
	}

	@Override
	public void emit(Node host, Message msg) {
		Node dest;
		Position hostPosition = ((PositionProtocolImpl) host.getProtocol(positionPid)).getCurrentPosition();
		for (int i = 0; i < Network.size(); i++) {
			dest = Network.get(i);
			if (dest.getID() == msg.getIdDest() || msg.getIdDest() == Emitter.ALL) {
				Position destPosition = ((PositionProtocolImpl) dest.getProtocol(positionPid)).getCurrentPosition();
				if (hostPosition != null && destPosition != null && hostPosition.distance(destPosition) <= scope) {
					EDSimulator.add(getLatency(), msg, dest, myPid);
				}
			}
		}
	}

	@Override
	public int getLatency() {
		if(varianceActive) {
			return CommonState.r.nextPoisson(latency);
		}
		return latency;
	}

	@Override
	public int getScope() {
		return scope;
	}
	
	public Object receive(Node sender, Message msg) {
		return null;
	}

	public Object clone() {
		EmitterImpl obj = null;
		try {
			obj = (EmitterImpl) super.clone();
		} 
		catch (CloneNotSupportedException e) {}
		return obj;
	}
}
