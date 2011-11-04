package co.pointred.fx.serverpush.core;

public interface IServerPush
{
    /**
     * API to send sendServerpush after construction of XML string.
     * 
     * @param neKey
     * @param neType
     * @param objects
     */
    public void sendServerPush(String neKey, String neType, Object... objects);
}
