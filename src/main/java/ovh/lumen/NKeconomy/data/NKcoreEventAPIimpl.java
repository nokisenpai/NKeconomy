package ovh.lumen.NKeconomy.data;

import ovh.lumen.NKcore.api.NKcoreEventAPI;
import ovh.lumen.NKcore.api.data.packet.*;
import ovh.lumen.NKeconomy.managers.AccountManager;

public class NKcoreEventAPIimpl implements NKcoreEventAPI
{
	@Override
	public void onDataReceive(ReceivedData receivedData)
	{
		if(receivedData.getPluginSource().equals(NKData.PLUGIN_NAME))
		{
			AccountManager.onDataReceived(receivedData.getData());
		}
	}

	@Override
	public void onErrorResponse(ErrorResponse errorResponse)
	{
		System.out.println(errorResponse.getServerSource());
		System.out.println(errorResponse.getPluginSource());
		System.out.println(errorResponse.getErrorResponseType().toString());
		System.out.println(errorResponse.getData());
		if(errorResponse.getErrorResponseType() == ErrorResponseType.UNKNOWN_PLAYER && errorResponse.getPluginSource().equals(NKData.PLUGIN_NAME))
		{
			AccountManager.onErrorReceived(errorResponse.getData());
		}
	}

	@Override
	public void onPlayersQueryResponse(PlayersQueryResponse allPlayersQueryResponse)
	{
		System.out.println(allPlayersQueryResponse.getServerSource());
		System.out.println(allPlayersQueryResponse.getPluginSource());
		System.out.println(allPlayersQueryResponse.getData());
		allPlayersQueryResponse.getPlayersInfo().forEach(playerInfo -> {
			System.out.println("#########################");
			System.out.println(playerInfo.getName());
			System.out.println(playerInfo.getUuid().toString());
			System.out.println(playerInfo.getServerName());
		});
	}

	@Override
	public void onServersQueryResponse(ServersQueryResponse allServersQueryResponse)
	{
		System.out.println(allServersQueryResponse.getServerSource());
		System.out.println(allServersQueryResponse.getPluginSource());
		System.out.println(allServersQueryResponse.getData());
		allServersQueryResponse.getServers().forEach(System.out::println);
	}
}
