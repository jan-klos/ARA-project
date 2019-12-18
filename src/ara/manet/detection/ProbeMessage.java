package ara.manet.detection;

import ara.util.Message;

public class ProbeMessage extends Message {
	public ProbeMessage(long idsrc, long iddest, int pid) {
		super(idsrc, iddest, pid);
	}
}
