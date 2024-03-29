package co.pointred.core.startup;

public enum RecoveryMsg
{
	// Ping msg to start with
	PING, PONG, 
	START_DATABASE_BACKUP, DATABASE_BACKUP_TAKEN, DATABASE_BACKUP_FAILED,
	SERVER_SOCKET_STARTED, SERVER_SOCKET_CLOSED,
	CLIENT_SOCKET_STARTED,CLIENT_SOCKET_CLOSED,
	FILE_TX_SUCCESS, FILE_TX_FAILED, FILE_RECEIVED_SUCCESS,
	BYEBYE,
	CONNECTION_ERROR;
}
