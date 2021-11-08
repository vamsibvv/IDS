package counters;

public class Flow{
	

	String flowId;
	String packetSize;
    int estimatedValue;
    
    public Flow(String flowId, String packetSize) {
    	this.flowId = flowId;
    	this.packetSize = packetSize;
    }
    
    public Flow(String flowId, String packetSize, int estimatedValue) {
    	this.flowId = flowId;
    	this.packetSize = packetSize;
    	this.estimatedValue = estimatedValue;
    }

	public String getFlowId() {
		return flowId;
	}

	public void setEstimatedValue(int estimatedValue) {
		this.estimatedValue = estimatedValue;
	}

	public String getPacketSize() {
		return packetSize;
	}

	public int getEstimatedValue() {
		return estimatedValue;
	}
    
	@Override
	public String toString() {
		return flowId + "\t\t"+ estimatedValue + "\t\t"+ packetSize ;
	}
   
}