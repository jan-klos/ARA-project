package ara.manet.detection;

import ara.util.Message;

public class ProbeAckMessage extends Message {
	public ProbeAckMessage(long idsrc, long iddest, int pid) {
		super(idsrc, iddest, pid);
	}
}
