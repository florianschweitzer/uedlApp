using UnityEngine;
using System.Collections;

public class SceneController : MonoBehaviour {

	private float framesToWait = 3;
	private float framesWaited = 0;
	private float rotationDelta = 0;
	private float speed = 100.0f;
	private GameObject focusObject = null;
	private float descrObjectX = 0;
	private float detailObjectX = 0;

	private float angleAnalysis = 0;
	private float angleDesign = 90;
	private float angleImpl = 180;
	private float angleDeployment = 270;

	private string[] detailStrings = {"detail1\nrow2\nrow3\nrow4", "detail2\nrow2\nrow3\nrow4", "detail3\nrow2\nrow3\nrow4", "detail4\nrow2\nrow3\nrow4", "detail5\nrow2\nrow3\nrow4","detail6\nrow2\nrow3\nrow4", "detail7\nrow2\nrow3\nrow4"};
	private string[] descrStrings = {"descr1", "descr2", "descr3", "desc4", "descr5","descr6", "descr7"};
	private GameObject descrObj = null;
	private int indexFront = 0;
	private float prevRotateTo = 0;
	private float prevRotateToPhases = 0;
	private bool isDetailRotating = false;
	private int currPhaseIndex = 0;

	private GameObject detailCube = null;
	private GameObject frontDetailObj = null;
	private GameObject leftDetailObj = null;
	private GameObject rightDetailObj = null;
	private GameObject backDetailObj = null;

	private GameObject currFront = null;
	private GameObject currLeft = null;
	private GameObject currRight = null;
	private GameObject currBack = null;

	private GameObject objAnalysis = null;
	private GameObject objDesign = null;
	private GameObject objImpl = null;
	private GameObject objDeployment = null;

	// Use this for initialization
	void Start () {	
		descrObj = GameObject.Find ("descrObject");
		descrObjectX = GameObject.Find ("roundcubeDescr").transform.position.x;
		detailObjectX = GameObject.Find ("roundcubeDetail").transform.position.x;

		detailCube = GameObject.Find ("roundcubeDetail");

		frontDetailObj = GameObject.Find ("detailFront");
		leftDetailObj = GameObject.Find ("detailLeft");
		rightDetailObj = GameObject.Find("detailRight");
		backDetailObj = GameObject.Find("detailBack");

		currFront = frontDetailObj;
		currLeft = leftDetailObj;
		currRight = rightDetailObj;
		currBack = backDetailObj;

		objAnalysis = GameObject.Find ("Analysis");
		objDesign = GameObject.Find ("Design");
		objImpl = GameObject.Find ("Implementation");
		objDeployment = GameObject.Find ("Deployment");
	}

	// Update is called once per frame
	void Update () {
		checkBackButton();
		checkDetailCube();

		if (Input.GetMouseButton(0)) {
			Ray mouseRay = Camera.main.ScreenPointToRay(Input.mousePosition);
			RaycastHit mouseHit = new RaycastHit();

			if (Physics.Raycast(mouseRay, out mouseHit)) {
				switch (mouseHit.collider.name) {
				case "roundcubeDetail":
					focusObject = GameObject.Find ("roundcubeDetail");
					isDetailRotating = true;
					if (framesWaited < framesToWait) {
						framesWaited++;
					}
					break;
				case "roundcubeTitle":
					focusObject = GameObject.Find ("roundcubeTitle");
					if (framesWaited < framesToWait) {
						framesWaited++;
					}
					break;
				case "roundcubeDescr":
					focusObject = GameObject.Find ("roundcubeDescr");					
					if (framesWaited < framesToWait) {
						framesWaited++;
					}
					break;
				}
			}

			if (focusObject != null && (focusObject.name == "roundcubeTitle" || focusObject.name == "roundcubeDetail") &&
			    framesWaited >= framesToWait) {
				focusObject.transform.Rotate(new Vector3(0, -Input.GetAxis("Mouse X"), 0) * Time.deltaTime * speed);
				rotationDelta += Mathf.Abs (Input.GetAxis("Mouse X"));
			}
		} else if (focusObject != null) {
			if (Input.GetMouseButtonUp(0)) {
				isDetailRotating = false;
				if (rotationDelta < 0.5) {
					if (focusObject.name == "roundcubeDetail") {
						descrObj.GetComponent<TextMesh>().text = descrStrings[indexFront];
						Debug.Log ("click on detail detected");
						iTween.MoveTo(Camera.main.gameObject, iTween.Hash ("x", descrObjectX,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
					} else if (focusObject.name == "roundcubeDescr") {
						Debug.Log ("click on descr detected");
						iTween.MoveTo(Camera.main.gameObject, iTween.Hash ("x", detailObjectX,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
					}
				}
				rotationDelta = 0;
			}


			float yRot = focusObject.transform.eulerAngles.y;
			float rotateTo = 0;

			if (yRot >= 320 || yRot <= 45) {
				rotateTo = 0;
			} else if (yRot > 45 && yRot <= 135) {
				rotateTo = 90;
			} else if (yRot > 135 && yRot <= 225) {
				rotateTo = 180;
			} else if (yRot > 225 && yRot < 320) {
				rotateTo = 270;
			}

			if (focusObject.name == "roundcubeDetail") {
				if ((prevRotateTo == 0 && rotateTo == 90) || (prevRotateTo == 90 && rotateTo == 180) ||
				    (prevRotateTo == 180 && rotateTo == 270) || (prevRotateTo == 270 && rotateTo == 0)) {
					//Debug.Log("turn to right");
					refreshDetailSettings(false);
				} else if (prevRotateTo != rotateTo) {
					//Debug.Log("turn to left");
					refreshDetailSettings(true);
				}

				prevRotateTo = rotateTo;
			} else if (focusObject.name == "roundcubeTitle") {
				if ((prevRotateToPhases == 0 && rotateTo == 90) || (prevRotateToPhases == 90 && rotateTo == 180) ||
				    (prevRotateToPhases == 180 && rotateTo == 270) || (prevRotateToPhases == 270 && rotateTo == 0)) {
					Debug.Log("phases turn to right");
					currPhaseIndex++;
				} else if (prevRotateToPhases != rotateTo) {
					Debug.Log("phases turn to left");
					currPhaseIndex--;
				}

				if (currPhaseIndex < 0) {
					currPhaseIndex = 3;
				} else if (currPhaseIndex > 3) {
					currPhaseIndex = 0;
				}

				callAndroidPhaseChange(currPhaseIndex);

				Debug.Log ("currPhaseIndex: " + currPhaseIndex);
				
				prevRotateToPhases = rotateTo;
			}
				
			iTween.RotateTo(focusObject, iTween.Hash("y",rotateTo,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
			focusObject = null;
			framesWaited = 0;
		}
	}

	void checkBackButton() {
		if (Input.GetKeyDown(KeyCode.Escape)) {
			Application.Quit();
		}
	}

	void refreshDetailSettings(bool turnLeft) {
		if (turnLeft) {
			indexFront--;
			if (currFront == frontDetailObj) {
				currFront = leftDetailObj;
			} else if (currFront == leftDetailObj) {
				currFront = backDetailObj;
			} else if (currFront == rightDetailObj) {
				currFront = frontDetailObj;
			} else if (currFront == backDetailObj) {
				currFront = rightDetailObj;
			}
		} else {
			indexFront++;
			if (currFront == frontDetailObj) {
				currFront = rightDetailObj;
			} else if (currFront == leftDetailObj) {
				currFront = frontDetailObj;
			} else if (currFront == rightDetailObj) {
				currFront = backDetailObj;
			} else if (currFront == backDetailObj) {
				currFront = leftDetailObj;
			}
		}

		if (indexFront < 0) {
			indexFront = detailStrings.Length - 1;
		} else if (indexFront > detailStrings.Length - 1) {
			indexFront = 0;
		}

		if (currFront == leftDetailObj) {
			currLeft = backDetailObj;
			currRight = frontDetailObj;
			currBack = rightDetailObj;
		} else if (currFront == backDetailObj) {
			currLeft = rightDetailObj;
			currRight = leftDetailObj;
			currBack = frontDetailObj;
		} else if (currFront == frontDetailObj) {
			currLeft = leftDetailObj;
			currRight = rightDetailObj;
			currBack = backDetailObj;
		} else if (currFront == rightDetailObj) {
			currLeft = frontDetailObj;
			currRight = backDetailObj;
			currBack = leftDetailObj;
		}

		callAndroidDetailChange(indexFront);
	}
	
	void checkDetailCube() {
		int leftIndex = 0;
		int rightIndex = 0;
		int backIndex = 0;

		leftIndex = indexFront-1;
		rightIndex = indexFront+1;
		backIndex = indexFront+2;

		if (leftIndex < 0) {
			leftIndex = detailStrings.Length - 1;
		}

		if (rightIndex == detailStrings.Length - 1) {
			backIndex = 0;
		}

		if (rightIndex == detailStrings.Length) {
			rightIndex = 0;
			backIndex = 1;
		}

		currFront.SetActive(true);

		if (isDetailRotating) {
			currLeft.SetActive(true);
			currRight.SetActive(true);
		} else {
			currLeft.SetActive(false);
			currRight.SetActive(false);
		}

		currBack.SetActive(false);



		currFront.GetComponent<TextMesh>().text = detailStrings[indexFront];
		currLeft.GetComponent<TextMesh>().text = detailStrings[leftIndex];
		currRight.GetComponent<TextMesh>().text = detailStrings[rightIndex];
		currBack.GetComponent<TextMesh>().text = detailStrings[backIndex];

		//Debug.Log ("index Front: " + indexFront + "; index left: " + leftIndex + "; index right: " + rightIndex + "; index back: " + backIndex);
	}

	void testMethod(string _testParam) {
		Debug.Log ("testMethod called with param: " + _testParam);
	}

	void rotateToAnalysis() {
		iTween.MoveTo(Camera.main.gameObject, iTween.Hash ("x", detailObjectX,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
		GameObject titleObj = GameObject.Find ("roundcubeTitle");
		iTween.RotateTo(titleObj, iTween.Hash("y",angleAnalysis,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
	}

	void rotateToDesign() {
		iTween.MoveTo(Camera.main.gameObject, iTween.Hash ("x", detailObjectX,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
		GameObject titleObj = GameObject.Find ("roundcubeTitle");
		iTween.RotateTo(titleObj, iTween.Hash("y",angleDesign,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
	}

	void rotateToImplementation() {
		iTween.MoveTo(Camera.main.gameObject, iTween.Hash ("x", detailObjectX,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
		GameObject titleObj = GameObject.Find ("roundcubeTitle");
		iTween.RotateTo(titleObj, iTween.Hash("y",angleImpl,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
	}

	void rotateToDeployment() {
		iTween.MoveTo(Camera.main.gameObject, iTween.Hash ("x", detailObjectX,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
		GameObject titleObj = GameObject.Find ("roundcubeTitle");
		iTween.RotateTo(titleObj, iTween.Hash("y",angleDeployment,"easeType", "easeOutSine", "time", 0.5f,"islocal",true));
	}

	void rotateToDetail(string _indexDetailStr) {
		int _indexDetail = int.Parse(_indexDetailStr);

		if (_indexDetail < detailStrings.Length) {
			bool found = false;
			
			for (int i = 0 ; i < detailStrings.Length ; i++) {
				if (indexFront == _indexDetail) {
					found = true;
					break;
				}
				
				refreshDetailSettings(false);
				detailCube.transform.Rotate(new Vector3(0,90,0));
				prevRotateTo = (prevRotateTo + 90) % 360;
			}
		}
	}

	void setDetailStrings(string _detailStrings) {
		Debug.Log ("received title string: \n" + _detailStrings);

		char[] sep = {'$'};
		string[] temp = _detailStrings.Split(sep);

		detailStrings = new string[temp.Length-1];

		for (int i = 0 ; i < temp.Length-1 ; i++) {
			detailStrings[i] = temp[i];
		}

		Debug.Log("received " + detailStrings.Length + " strings!");
	}

	void setDescrStrings(string _descrStrings) {
		Debug.Log ("received descr string: \n" + _descrStrings);
		
		char[] sep = {'$'};
		string[] temp = _descrStrings.Split(sep);

		descrStrings = new string[temp.Length-1];

		for (int i = 0 ; i < temp.Length -1 ; i++) {
			descrStrings[i] = temp[i];
		}
		
		Debug.Log("received " + descrStrings.Length + " strings!");
	}

	void setPhasesString(string _phases) {
		char[] sep = {'$'};
		string[] temp = _phases.Split(sep);
		
		string[] temp2 = new string[temp.Length-1];
		
		for (int i = 0 ; i < temp.Length -1 ; i++) {
			temp2[i] = temp[i];
		}

		objAnalysis.GetComponent<TextMesh>().text = temp2[0];
		objDesign.GetComponent<TextMesh>().text = temp2[1];
		objImpl.GetComponent<TextMesh>().text = temp2[2];
		objDeployment.GetComponent<TextMesh>().text = temp2[3];
	}

	void callAndroidPhaseChange(int _index) {
		using (AndroidJavaClass cls_UnityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
			using(AndroidJavaObject obj_Activity = cls_UnityPlayer.GetStatic<AndroidJavaObject>("currentActivity")) {
				obj_Activity.Call("phaseChanged",_index);
			}
		}
	}

	void callAndroidDetailChange(int _index) {
		using (AndroidJavaClass cls_UnityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer")) {
			using(AndroidJavaObject obj_Activity = cls_UnityPlayer.GetStatic<AndroidJavaObject>("currentActivity")) {
				obj_Activity.Call("detailChanged",_index);
			}
		}
	}


}
