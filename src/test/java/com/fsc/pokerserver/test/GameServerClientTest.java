package com.fsc.pokerserver.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fcs.pokerserver.gameserver.MqttServletGameServer;



/*
 * Url to get content
 * create player
 * http://localhost:8080/api/register?username=hbg1&password=123456
 * 
 * player login
 * http://localhost:8080/api/login?username=hbg1&password=123456
 * 
 * create room
 * http://localhost:8080/api/room?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImhiZzEifQ.czIr3dIp9wMDzKwDzeun_a8eU8LizqA2urjctiUNT4M&method=put
 * 
 * get list room
 * http://localhost:8080/api/room?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImhiZzEifQ.czIr3dIp9wMDzKwDzeun_a8eU8LizqA2urjctiUNT4M&method=get
 * 
 * player join room
 * http://localhost:8080/api/room?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImhiZzEifQ.czIr3dIp9wMDzKwDzeun_a8eU8LizqA2urjctiUNT4M&method=join&id=1531887589128
 * 
 *  player start game in room
 * http://localhost:8080/api/game?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImhiZzEifQ.czIr3dIp9wMDzKwDzeun_a8eU8LizqA2urjctiUNT4M&method=start
 * 
 * 
 * 
 * List token of players.
 * String arr[]= {"gio1","hbg1","poke1","agru1","kuki1"};
 * gio1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImdpbzEifQ.LUgFtexXVwBXQDPi3acL02tdpXZ4dtlNW7E700jilkI
 * hbg1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImhiZzEifQ.czIr3dIp9wMDzKwDzeun_a8eU8LizqA2urjctiUNT4M
 * poke1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InBva2UxIn0.3Hk9VnA32dhgk5sA_LnKENnyTkJ4pQIL1GF1HDetEDc
 * agru1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImFncnUxIn0._NXg4_vRYyu8ntaHROfVdu8snHxGirmzNlMav-96fZ4
 * kuki1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6Imt1a2kxIn0.1ZOy2qKYzZBEutMHFQtxmKoeKRv8DL9Pk90RQq_boOY
 * 
 * String arr[]= {"loi1","xeng1","thuy1","hoan1","lam1"};
 * loi1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImxvaTEifQ.jUv01rg6O6SPHNEVhf1a_J-MGcbpmCpWDH4IClM0RvM
 * xeng1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InhlbmcxIn0.HkyHGW5Ysq2gPml9fip2fWhzTCBdIL1AZKPMUHxTiUk
 * thuy1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InRodXkxIn0.TGcPMGnkg1ju9TFYGNWZflUWUe0CR7Y0BOi1dCuFVxY
 * hoan1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImhvYW4xIn0.w5oLx-cK4fZAtNCkbTl76EVBdU9jTVOvO8x-IyGsuak
 * lam1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImxhbTEifQ.t3Acur_pZd3oyWkOjggtdhrfr7_J0iMDRf1wKY_zXuE
 * 
 * String arr[]= {"tit1","thoa1","hung1","ngoc1","tuan1"};
 * tit1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InRpdDEifQ.qpOQ6sLLzhdyX-bl7H4cEi-le-cz2QuZe2ZhVOaH-Ls
 * thoa1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InRob2ExIn0.zI2dgBUrByb4jlrr7e0ckl5D6ap2fETPqJyPcQn_a4Y
 * hung1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6Imh1bmcxIn0.0retLNeTFIaBkiuEwxTHLhxvDODY7XgNcSvDIpoZTRc
 * ngoc1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6Im5nb2MxIn0.x4822cSRQYEhShAGrhMmCQgngOF1gQAcrHh-MQ4yPmI
 * tuan1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InR1YW4xIn0.qmQTFu3pyjSp6NpHPGpMyKgS07h0qV2LWK5YSwrb24I
 * 
 * String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
 * toan1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InRvYW4xIn0.o3GtRxDLak4IOOIZn6bwjn1YhUzzszUC9FhP4uidqrE
 * danh1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImRhbmgxIn0.8OSrcMXE75vDSAr3pd-RfRDaeippbEUqVCMwmcLgl14
 * linh1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImxpbmgxIn0.cOBCa1n6q3QYuUVFgEJBPcRxuKwnJtgV5j_kO3_qBDo
 * chau1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImNoYXUxIn0.nuXZBVlnVBZPHP0IdQ1YcFDIlaNNwQBcvxibgF8qN94
 * nghe1: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6Im5naGUxIn0.3Bx_3efkx4UYEuHwVz2CCSNEIS6i6qeRN89o6xafAaY
 * 
 * **/

public class GameServerClientTest implements MqttCallback {

	
	@Before
	public void setUp() throws Exception {
		MqttServletGameServer mqttServletGameServer = MqttServletGameServer.getInstance();
		Thread.sleep(2000);
	}
	
	/*
	 * Get content from the url
	 * */
	public String getContentFromUrl(String url)  throws ClientProtocolException, IOException
	{
		String content="";
		
		HttpClient client = new DefaultHttpClient();
		
		HttpGet request = new HttpGet(url);
		HttpResponse response  = client.execute(request);
		
		DataInputStream rd = new DataInputStream(
				response.getEntity().getContent());
		
		
		content = rd.readLine();
		return content;
	}
	
	/*
	 * Get token from Player
	 * */
	public String[] getTokenPlayer() throws ClientProtocolException, IOException
	{
//		String arr[]= {"gio1","hbg1","poke1","agru1","kuki1"};
//		String arr[]= {"loi1","xeng1","thuy1","hoan1","lam1"};
//		String arr[]= {"tit1","thoa1","hung1","ngoc1","tuan1"};
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};

		String token[]= new String[5];
	
		for(int i = 0;i<arr.length;i++)
		{
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet("http://localhost:8080/api/login?username="+arr[i]+"&password=123456");
//			HttpGet request = new HttpGet("http://localhost:8080/api/login?username=hbg1&password=123456");
			HttpResponse response  = client.execute(request);
			
			DataInputStream rd = new DataInputStream(
					response.getEntity().getContent());
			
			token[i]=rd.readLine();
		}
//		System.out.println("length of token: "+token.length);
		return token;
	}
	
	/*
	 * Check value is Numeric
	 * */
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	/*
	 * Create player
	 * */
//	@Test
//	public void testCreatePlayer()  throws IOException, ClientProtocolException{
////		String arr[]= {"gio1","hbg1","poke1","agru1","kuki1"};
////		String arr[]= {"loi1","xeng1","thuy1","hoan1","lam1"};
////		String arr[]= {"tit1","thoa1","hung1","ngoc1","tuan1"};
//		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
//
//		for(int i=0;i<arr.length;i++)
//		{
//			String url = "http://localhost:8080/api/register?username="+arr[i]+"&password=123456";
//			this.getContentFromUrl(url);
//		}
//		
//	}

	
	/*
	 * Create player but player is exist
	 * */
	@Test(expected = AssertionError.class) @Ignore
	public void testCreatePlayerExist()  throws IOException, ClientProtocolException{
		String username = "hbg1";
		String url = "http://localhost:8080/api/register?username="+username+"&password=123456";
		assertEquals(this.getContentFromUrl(url), username);
		
	}
	
	
	/*
	 * Player login
	 * */
	@Test @Ignore
	public void testLogin() throws ClientProtocolException, IOException  {
//		create player array to get token array of player to join room.
//		String arr[]= {"gio1","hbg1","poke1","agru1","kuki1"};
//		String arr[]= {"loi1","xeng1","thuy1","hoan1","lam1"};
//		String arr[]= {"tit1","thoa1","hung1","ngoc1","tuan1"};
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};

		for(int i = 0;i<arr.length;i++)
		{
			String url = "http://localhost:8080/api/login?username="+arr[0]+"&password=123456";
			this.getContentFromUrl(url);
		}
	}
	
	/*
	 * Player login error
	 * */
	@Test(expected = AssertionError.class) @Ignore
	public void testLoginWithPlayerNotRegister() throws ClientProtocolException, IOException  {

		String username = "mai";
		String url = "http://localhost:8080/api/login?username="+username+"&password=123456";
		this.getContentFromUrl(url);
		assertEquals(this.getContentFromUrl(url), "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImFncnUxIn0._NXg4_vRYyu8ntaHROfVdu8snHxGirmzNlMav-96fZ4");
	}
	
	/*
	 * Create room
	 * */
	@Test @Ignore
	public void testCreateRoom()   throws IOException, ClientProtocolException{
		//Array of Players
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
		// Array Token of Players
		String token[] = this.getTokenPlayer();
		
		//Player login
		for(int i = 0;i<arr.length;i++)
		{
			String url = "http://localhost:8080/api/login?username="+arr[0]+"&password=123456";
			this.getContentFromUrl(url);
		}
		
		//create room
		String url = "http://localhost:8080/api/room?token="+token[0]+"&method=put";
		this.getContentFromUrl(url);
	
		
		
	}
	
	
	/*
	 * Create room With Token not login or Not Exist
	 * */
	@Test(expected = AssertionError.class)@Ignore
	public void testCreateRoomTokenError()   throws IOException, ClientProtocolException{
		//Array of Players
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
		
		//Player login
		for(int i = 0;i<arr.length;i++)
		{
			String url = "http://localhost:8080/api/login?username="+arr[0]+"&password=123456";
			this.getContentFromUrl(url);
		}
		
		//create room
		// Player have token that Player dont login
		String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InRpdDEifQ.qpOQ6sLLzhdyX-bl7H4cEi-le-cz2QuZe2ZhVOaH-Ls";
		String url = "http://localhost:8080/api/room?token="+token+"&method=put";
		assertEquals(isNumeric(this.getContentFromUrl(url)), true);	
	}
	
	
	/*
	 * Join room.
	 * */
	@Test
	public void testJoinRoom()  throws IOException, ClientProtocolException{
		// Array Token of Players
		String token[] = this.getTokenPlayer();
		//Array of Players
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
		
		//Player login
		for(int i = 0;i<arr.length;i++)
		{
			String url = "http://localhost:8080/api/login?username="+arr[0]+"&password=123456";
			this.getContentFromUrl(url);
		}
		
		//create room
		String urlCreateRoom = "http://localhost:8080/api/room?token="+token[0]+"&method=put";
		String id = this.getContentFromUrl(urlCreateRoom);
		
		//join room
		for(int i=0;i<token.length;i++)
		{
			String url = "http://localhost:8080/api/room?token="+token[i]+"&method=join&id="+id;
			this.getContentFromUrl(url);
//			System.out.println(this.getContentFromUrl(url));
		}
		
	}
	
	/*
	 * Join room With token error.
	 * */
	@SuppressWarnings("deprecation")
	@Test(expected = AssertionError.class)@Ignore
	public void testJoinRoomWithTokenError()  throws IOException, ClientProtocolException{
		// Array Token of Players
		String token[] = this.getTokenPlayer();
		//Array of Players
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
		
		//Player login
		for(int i = 0;i<arr.length;i++)
		{
			String url = "http://localhost:8080/api/login?username="+arr[0]+"&password=123456";
			this.getContentFromUrl(url);
		}
		
		//create room
		String urlCreateRoom = "http://localhost:8080/api/room?token="+token[0]+"&method=put";
		String id = this.getContentFromUrl(urlCreateRoom);
		
		//join room
		String titToken= "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6InRpdDEifQ.qpOQ6sLLzhdyX-bl7H4cEi-le-cz2QuZe2ZhVOaH-Ls";
		String urlJoin = "http://localhost:8080/api/room?token="+titToken+"&method=join&id="+id;
		this.getContentFromUrl(urlJoin);
		float lengthUrlJoinFirst = this.getContentFromUrl(urlJoin).length();
		float sumLengthUrlJoin = 0;
		for(int i=1;i<token.length;i++)
		{
			String url = "http://localhost:8080/api/room?token="+token[i]+"&method=join&id="+id;
			this.getContentFromUrl(url);
			sumLengthUrlJoin+=this.getContentFromUrl(url).length();
		}
		float value = sumLengthUrlJoin/(token.length-1);
		assertEquals(value, lengthUrlJoinFirst);
		
	}
	

	/*
	 * Join room with room id error.
	 * */
	@Test(expected = AssertionError.class)
	public void testJoinRoomWithRoomIdError()  throws IOException, ClientProtocolException{
		// Array Token of Players
		String token[] = this.getTokenPlayer();
		//Array of Players
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
		
		//Player login
		for(int i = 0;i<arr.length;i++)
		{
			String url = "http://localhost:8080/api/login?username="+arr[0]+"&password=123456";
			this.getContentFromUrl(url);
		}
		
		//create room to get room id
		//room id error
		String id = "1532594321491";
		int sumOfLength=0;
		//join room
		for(int i=0;i<token.length;i++)
		{
			String url = "http://localhost:8080/api/room?token="+token[i]+"&method=join&id="+id;
			this.getContentFromUrl(url);
			sumOfLength+= this.getContentFromUrl(url).length();
		}
		int value = sumOfLength/token.length;
		assertNotEquals(value, 6);
	}
	
	/*
	 * Get list of room
	 * */
	@Test @Ignore
	public void testGetListRooms()  throws IOException, ClientProtocolException{
		// Array Token of Players
		String token[] = this.getTokenPlayer();
		//Array of Players
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
		
		//Player login
		for(int i = 0;i<arr.length;i++)
		{
			String url = "http://localhost:8080/api/login?username="+arr[0]+"&password=123456";
			this.getContentFromUrl(url);
		}
		
		//create room
		String urlCreateRoom = "http://localhost:8080/api/room?token="+token[0]+"&method=put";
		String id = this.getContentFromUrl(urlCreateRoom);
		
		//join room
		for(int i=0;i<token.length;i++)
		{
			String url = "http://localhost:8080/api/room?token="+token[i]+"&method=join&id="+id;
			this.getContentFromUrl(url);
		}
		
		String urlGetListRoom = "http://localhost:8080/api/room?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJwb2tlcnNlcnZlciIsImp0aSI6ImdpbzEifQ.LUgFtexXVwBXQDPi3acL02tdpXZ4dtlNW7E700jilkI&method=get";
//		System.out.println(this.getContentFromUrl(urlGetListRoom));
		this.getContentFromUrl(urlGetListRoom);
	}
	
	/*
	 * Start Game in Room
	 * */
	@Test @Ignore
	public void testStartGame()  throws IOException, ClientProtocolException{
		// Array of Players
		String arr[]= {"toan1","danh1","linh1","chau1","nghe1"};
		// Array Token of Players
		String token[] = this.getTokenPlayer();
		
		//Player login

		for(int i = 0;i<arr.length;i++)
		{
			String url = "http://localhost:8080/api/login?username="+arr[0]+"&password=123456";
			this.getContentFromUrl(url);
		}
		
		//create room
		String urlCreateRoom = "http://localhost:8080/api/room?token="+token[0]+"&method=put";
		String id = this.getContentFromUrl(urlCreateRoom);
		
		//join room
		String createRoom = "http://localhost:8080/api/room?token="+token[0]+"&method=put";
		String idRoom = this.getContentFromUrl(createRoom);
		for(int i=0;i<token.length;i++)
		{
			String url = "http://localhost:8080/api/room?token="+token[i]+"&method=join&id="+idRoom;
			this.getContentFromUrl(url);
		}
		
		//startgame
		String startGame = "http://localhost:8080/api/game?token="+token[0]+"&method=start";
		this.getContentFromUrl(startGame);
//		System.out.println(this.getContentFromUrl(startGame));
	
	}
	
	

	@Override
	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stubs
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}

}
