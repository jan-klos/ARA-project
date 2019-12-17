package ara.util;

public class ProbeMessage extends Message {
	private final int timer;
	private final int probe;
	
	public ProbeMessage(long idsrc, long iddest, int pid, int timer, int probe) {
		super(idsrc, iddest, pid);
		this.timer = timer;
		this.probe = probe;
	}

	public int getTimer() {
		return timer;
	}
	
	public int getProbe() {
		return probe;
	}
	
}