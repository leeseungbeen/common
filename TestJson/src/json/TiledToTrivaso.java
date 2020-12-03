package json;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TiledToTrivaso implements IConvertingJson{

	JSONObject totalJsonObj;
	 
	
	/*������*/
	public TiledToTrivaso(){		
	}

	/*Json ���� �������ϴ� �������̽� �Լ� ����*/
	@Override
	public void convertingJson(String path,String targetName, String resultName) {
		
		//��� Json ���� ����ȭ.
		createResultJsonObj();
		
		//Tild���� ������ ���� Json read.
		JSONObject     returnVal     = readTiledOrgJsonObj(targetName);
		
		if (null != returnVal) {
			
			//1.Objects ���� ������
			calcObjectsInfo(returnVal);
			
			//2.Layer���� ����
			calcLayerInfo(returnVal);
			
			
//			//Tivaso Json���� ����
			JsonController tiledJsonCtrl = JsonController.getInstance();
	    	try {
				tiledJsonCtrl.writeJson(path, resultName,totalJsonObj);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/*������� ���� json object ���� ����*/
	private void createResultJsonObj() {
		
		//root ��� �߰�.
		totalJsonObj          = new JSONObject();
		JSONObject objectsObj = new JSONObject();
		JSONObject initialObj = new JSONObject();
		JSONObject singleObj  = new JSONObject();
		
		JSONArray groundMap    = new JSONArray();
		JSONArray objectsMap   = new JSONArray();
		
		totalJsonObj.put(ConstDefine.JSON_KEY_OBJECTS,objectsObj);
		totalJsonObj.put(ConstDefine.JSON_KEY_INIT_CONTROLLABLE_LOCATION,initialObj);
		totalJsonObj.put(ConstDefine.JSON_KEY_SINGLE_GROUND_IMG,singleObj);
		totalJsonObj.put(ConstDefine.JSON_KEY_GROUND_MAP,groundMap);
		totalJsonObj.put(ConstDefine.JSON_KEY_OBJECT_MAP,objectsMap);
	}
	
	/* Tiled Map���� ������ json ����� �о�´�.*/
	private JSONObject readTiledOrgJsonObj(String targetName) {
		
		JSONObject     returnVal     = null;
		JsonController tiledJsonCtrl = JsonController.getInstance();
		
		try {
    		tiledJsonCtrl.readJson(targetName);
    		returnVal = tiledJsonCtrl.getTotalJsonObject(targetName);    				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnVal;
	}
	
	/* Objects ������ ������.*/
	private void calcObjectsInfo(JSONObject tiledJson) {
		
		JSONArray tilesetsObj = (JSONArray)tiledJson.get(ConstDefine.JSON_KEY_TILESET);    
		
		if (tilesetsObj != null) {
			
			for (int i = 0; i < tilesetsObj.size(); i++) {
				
				//tilesets ������ Ȱ���Ͽ� objects ������ ����ó��.
				JSONObject tileSetObj     = (JSONObject)tilesetsObj.get(i);
				JSONArray tilesObj        = (JSONArray)tileSetObj.get(ConstDefine.JSON_KEY_TILES);
				
				boolean isObject      	  = true;
				boolean isBG              = false;
				
				long firstTileId          = (Long) tileSetObj.get(ConstDefine.JSON_KEY_FIRST_GID);
				
				String  imagePath        = (String)tileSetObj.get(ConstDefine.JSON_KEY_IMAGE);
				boolean isMovable        = false;
				boolean isInteractive    = false;
				boolean isNoTransParency = false;
				boolean isFloor          = false;
				long     rowSpan         = 1;
				long     columnSpan      = 1;
				long     scale           = 1;
				
				//Tileset �ϳ��� �������� tile ������ �� ���� ���� ����.
				if (tilesObj != null) {						
							
					//firstGID �������� ID ���� �����ָ� ���� �ش� Object�� id���� ��.
					for (int toidx = 0; toidx < tilesObj.size(); toidx++) {
						
						JSONObject customInfoObj = (JSONObject) tilesObj.get(toidx);
															
						long id                  = firstTileId + (Long)customInfoObj.get(ConstDefine.JSON_KEY_ID);
						JSONArray properties     = (JSONArray) customInfoObj.get(ConstDefine.JSON_KEY_PROPERTIES);
						
				
						isMovable        = false;
	    				isInteractive    = false;
	    				isNoTransParency = false;
	    				isFloor          = false;
	    				rowSpan         = 1;
	    				columnSpan      = 1;
	    				scale           = 1;
	    				
						for (int pi = 0; pi < properties.size(); pi++) {
							JSONObject piObj = (JSONObject) properties.get(pi);
							String piObjName = (String)piObj.get(ConstDefine.JSON_KEY_NAME);
							
							if (piObjName.equals(ConstDefine.JSON_KEY_IS_OBJECT)) {
								isObject = (Boolean)piObj.get(ConstDefine.JSON_KEY_VALUE);								
							}	
							else if (piObjName.equals(ConstDefine.JSON_KEY_IS_BG)) {
								isBG     = (Boolean)piObj.get(ConstDefine.JSON_KEY_VALUE);		
							}
							else if (piObjName.equals(ConstDefine.JSON_KEY_SCALE)) {
								scale     = (Long)piObj.get(ConstDefine.JSON_KEY_VALUE);		
							}
							else if (piObjName.equals(ConstDefine.JSON_KEY_IS_MOVEABLE)) {
								isMovable =(Boolean)customInfoObj.get(ConstDefine.JSON_KEY_VALUE);
							}
							else if (piObjName.equals(ConstDefine.JSON_KEY_IS_INTERACTIVE)) {
								isInteractive =(Boolean)customInfoObj.get(ConstDefine.JSON_KEY_VALUE);
							}
							else if (piObjName.equals(ConstDefine.JSON_KEY_IS_NO_TRANSPARENCY)) {
								isNoTransParency =(Boolean)customInfoObj.get(ConstDefine.JSON_KEY_VALUE);
							}
							else if (piObjName.equals(ConstDefine.JSON_KEY_IS_FLOOR)) {
								isFloor =(Boolean)customInfoObj.get(ConstDefine.JSON_KEY_VALUE);
							}
							else if (piObjName.equals(ConstDefine.JSON_KEY_ROW_SPAN)) {
								rowSpan =(Long)customInfoObj.get(ConstDefine.JSON_KEY_VALUE);
							}
							else if (piObjName.equals(ConstDefine.JSON_KEY_COLUMN_SPAN)) {
								columnSpan =(Long)customInfoObj.get(ConstDefine.JSON_KEY_VALUE);
							}
						
						}
						
						//Object�� �ش��ϸ� Object ���� ����
						if (isObject) {
							
							JSONObject targetObj  = new JSONObject();
		    				
		    				//���� ����
		    				JSONObject visualObj  = new JSONObject();
		    				JSONObject idleObj    = new JSONObject();
		    				JSONArray  framesArr  = new JSONArray();    				
		    				targetObj.put(ConstDefine.JSON_KEY_VISUALS, visualObj);
		    				visualObj.put(ConstDefine.JSON_KEY_IDLE, idleObj);
		    				idleObj.put(ConstDefine.JSON_KEY_FRAMES, framesArr);		    				    				
		    				targetObj.put(ConstDefine.JSON_KEY_IS_MOVEABLE, isMovable);
		    				targetObj.put(ConstDefine.JSON_KEY_IS_INTERACTIVE, isInteractive);
		    				targetObj.put(ConstDefine.JSON_KEY_IS_NO_TRANSPARENCY, isNoTransParency);
		    				targetObj.put(ConstDefine.JSON_KEY_IS_FLOOR	, isFloor);
		    				targetObj.put(ConstDefine.JSON_KEY_ROW_SPAN, rowSpan);
		    				targetObj.put(ConstDefine.JSON_KEY_COLUMN_SPAN, columnSpan);
		    				
		    				
		    				JSONObject objectsObj =(JSONObject)totalJsonObj.get(ConstDefine.JSON_KEY_OBJECTS);
		    				
		    				//object ���� ����
		    				if (objectsObj != null) {		    						    			
			    				objectsObj.put(""+ id, targetObj);
		    				}
		    				
						}
						
						JSONObject bgObj   = (JSONObject)totalJsonObj.get(ConstDefine.JSON_KEY_SINGLE_GROUND_IMG);
						
						//bg ���� ����.
						if (isBG && null != bgObj) {
							String img_path = (null != imagePath)? imagePath:"";			
							bgObj.put(ConstDefine.JSON_KEY_PATH, img_path);
							bgObj.put(ConstDefine.JSON_KEY_SCALE, scale);
						}
					}
				}
				else {
					//�ϳ��� ������ �ִٰ� ���� ��.
					
					JSONObject targetObj  = new JSONObject();
    				
    				//���� ����
    				JSONObject visualObj  = new JSONObject();
    				JSONObject idleObj    = new JSONObject();
    				JSONArray  framesArr  = new JSONArray();    				
    				targetObj.put(ConstDefine.JSON_KEY_VISUALS, visualObj);
    				visualObj.put(ConstDefine.JSON_KEY_IDLE, idleObj);
    				idleObj.put(ConstDefine.JSON_KEY_FRAMES, framesArr);		    				    				
    				targetObj.put(ConstDefine.JSON_KEY_IS_MOVEABLE, isMovable);
    				targetObj.put(ConstDefine.JSON_KEY_IS_INTERACTIVE, isInteractive);
    				targetObj.put(ConstDefine.JSON_KEY_IS_NO_TRANSPARENCY, isNoTransParency);
    				targetObj.put(ConstDefine.JSON_KEY_IS_FLOOR	, isFloor);
    				targetObj.put(ConstDefine.JSON_KEY_ROW_SPAN, rowSpan);
    				targetObj.put(ConstDefine.JSON_KEY_COLUMN_SPAN, columnSpan);
    				
    				
    				JSONObject objectsObj =(JSONObject)totalJsonObj.get(ConstDefine.JSON_KEY_OBJECTS);
    				
    				//object ���� ����
    				if (objectsObj != null) {		    						    			
	    				objectsObj.put(""+ firstTileId, targetObj);
    				}
					
				}
			}
		}
	}
	
	
	/* ĳ���� ����, object ����, ground����, bg������ ������.*/
	private void calcLayerInfo(JSONObject tiledJson) {
		
		long       tileWidth    = (long)tiledJson.get(ConstDefine.JSON_KEY_TILE_WIDTH);
		long       tileHeight   = (long)tiledJson.get(ConstDefine.JSON_KEY_TILE_HEIGHT);
		long       allWidth     = (long)tiledJson.get(ConstDefine.JSON_KEY_WIDTH);
		long       allHeight    = (long)tiledJson.get(ConstDefine.JSON_KEY_HEIGHT);
		
		JSONObject initialObj = (JSONObject)totalJsonObj.get(ConstDefine.JSON_KEY_INIT_CONTROLLABLE_LOCATION);		
		JSONArray groundMap   = (JSONArray)totalJsonObj.get(ConstDefine.JSON_KEY_GROUND_MAP);
		JSONArray objectsMap  = (JSONArray)totalJsonObj.get(ConstDefine.JSON_KEY_OBJECT_MAP);
	
		
		JSONObject charactorLayerObj = null;
		JSONObject groundLayerObj 	 = null;
		ArrayList<JSONObject> objectLayerArr = new ArrayList<JSONObject>();
		
		//Layer���� ���ͼ� �� �ʿ��� ���̾������� �����Ѵ�.
		JSONArray layerJsonArr = (JSONArray)tiledJson.get(ConstDefine.JSON_KEY_LAYERS);    	
		for (int i = 0; i < layerJsonArr.size(); i++) {
			JSONObject fromObj    = (JSONObject)layerJsonArr.get(i);
			String name = (String)fromObj.get(ConstDefine.JSON_KEY_NAME);
			
			if (name.indexOf(ConstDefine.JSON_KEY_OBJECT + "_") > -1) {
				//object_�� layer�� �����ϰ� �ϳ��� �����ؼ� �ִ´�.
				objectLayerArr.add(fromObj);
			}
			else if (name.equals(ConstDefine.JSON_KEY_CHARACTOR_LAYER)) {
				charactorLayerObj = fromObj;
			}
			else if (name.equals(ConstDefine.JSON_KEY_GROUND_LAYER)) {
				groundLayerObj = fromObj;
			}			
		}
		
		// charactor ���� ����
		double columnIdx   = 10;
		double rowdx       = 10;
		long charactorId = 1;
		
		if (null != charactorLayerObj) {
			
			JSONArray charObjects = (JSONArray)charactorLayerObj.get(ConstDefine.JSON_KEY_OBJECTS);
			
			JSONObject charObj    = (JSONObject) charObjects.get(0);
			
			charactorId 		  = (Long)charObj.get(ConstDefine.JSON_KEY_GID);
			columnIdx             = (Double)charObj.get(ConstDefine.JSON_KEY_X);
			rowdx	              = (Double)charObj.get(ConstDefine.JSON_KEY_Y);
			
			columnIdx             = (long) Math.floor(columnIdx / tileWidth);
			rowdx                 = (long) Math.floor(rowdx / tileHeight);
//			columnIdx             = (long) (allHeight - Math.floor(columnIdx / tileWidth));
//			rowdx                 = (long) (allWidth - Math.floor(rowdx / tileHeight));
		}
		
		initialObj.put(ConstDefine.JSON_KEY_COLUMN_IDX, columnIdx);
		initialObj.put(ConstDefine.JSON_KEY_ROW_IDX, rowdx);
		initialObj.put(ConstDefine.JSON_KEY_CONTROLLABLE_ID, "" + charactorId);
		
		
		// ground map  ���� ����
		if (null != groundLayerObj) {
			
			JSONArray targetArr  = (JSONArray)groundLayerObj.get("data");   
			long rowCount   	 = (long)groundLayerObj.get("height");
			long columnCount	 = (long)groundLayerObj.get("width");
			
			for (int gi = 0; gi < columnCount; gi++) {
				
				String tmpStr = "";
				
				for (int gj = 0; gj < rowCount; gj++) {
					
					int strIdx = gj + (gi * (int)rowCount); 
					
					if (gj > 0) {
						tmpStr += ",";
					}
					
					int addVal = (((long)targetArr.get(strIdx)) > 0)?1:0;
					tmpStr += addVal; 
				}
				
				JSONObject rowJsonObj    = new JSONObject();
				rowJsonObj.put("row", tmpStr);
				groundMap.add(rowJsonObj);
			}    				
		}
		
		// objects map  ���� ����
		if (null != objectLayerArr) {
			
			long[][] resultObjectMap = new long[(int) allWidth][(int) allHeight];
			
			//object layer���� ������ ����
			for (int objIdx = 0; objIdx < objectLayerArr.size(); objIdx++) {
				JSONObject objectLayerObj = (JSONObject)objectLayerArr.get(objIdx);
				JSONArray targetArr = (JSONArray)objectLayerObj.get("data");   
					
				for (int gi = 0; gi < allWidth; gi++) {
										
					for (int gj = 0; gj < allHeight; gj++) {
						
						int strIdx = gj + (gi * (int)allHeight);						
						resultObjectMap[gi][gj] = (long)targetArr.get(strIdx);
					}
					
				}   
				
			}
			
			//����� map ����
			for (int gi = 0; gi < allWidth; gi++) {
				
				String tmpStr = "";
				
				for (int gj = 0; gj < allHeight; gj++) {					
					
					if (gj > 0) {
						tmpStr += ",";
					}
					
					tmpStr += resultObjectMap[gi][gj]; 
				}
				
				JSONObject rowJsonObj    = new JSONObject();
				rowJsonObj.put("row", tmpStr);
				objectsMap.add(rowJsonObj);
			}   
			 				
		}    		
		
	}
	
}
