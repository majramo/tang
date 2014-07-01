package dtos.base


class LoopHelper {
	def returnList = []
	int size
	String type
	int interval
	int max
	public  Object  getRand  (list , number){
		return getRand  (list , number, true)
	}

	public  Object  getRand  (list , number, unique){
		
		def tempList = []
		if(list.size() == 0){
			return tempList
		}
		def listunique = list.unique()
		def newItem = list[new Random().nextInt(list.size)]

		if(number==0) {
			tempList.add(newItem)
		}else {
			def counter = 0
			if(unique){
				if(listunique.size < number){
					number = listunique.size
				} 
			}
			while(counter<number) {
				if(unique){
					newItem = listunique[new Random().nextInt(listunique.size)]
					if(!tempList.contains(newItem)){
						tempList.add(newItem)
						counter++
					}
				}else{
					newItem = list[new Random().nextInt(list.size)]
					tempList.add(newItem)
					counter++
				}
				
			}
		}

		 
		return tempList
		 
	} 
 
	public Object setLoopHelper(int size,String type="all", int intervall=0, int max = 0) {
		//print "\n$size	type: $type, $interval "
		this.size = size
		this.type = type
		this.interval = intervall
		this.max = max
		return doStuff()
	}

	public Object doStuff(){
		//print "\n$size	type: $type, $interval "
		returnList = []
		if(size==0){
			return returnList
		}
		type = type.toLowerCase()
		def rand = new Random()
		int newRandom = 0

		if(type.equals("every")){
			if(interval<1 || interval>10){
				type = "all"
			}
		}

		if(type.equals("random")){
			if(interval<2 || interval>10){
				interval =2
			}
		}

		int counter = 0
		//print "\n$size	type: $type, $interval "
		(1..size).each{it->
			counter++
			if(type.equals("random") && newRandom == 0){
				newRandom = rand.nextInt(interval)	+ 2
				//log.info "new newRandom	$newRandom"
			}
			switch (type){
				case "even":
				if ((counter %2 == 0)){
					returnList.add (it-1)
				}
				break

				case "odd":
				if ((counter %2 != 0)){
					returnList.add (it-1)
				}
				break

				case "every":
				if ((counter ==1 || ((counter-1) % interval) == 0)){
					returnList.add (it-1)
				}
				break

				case "random":
				if (counter ==1){
					returnList.add (it-1)
				}else{
					if (((counter-1) % newRandom) == 0){
						returnList.add (it-1)
						newRandom = 0
					}
				}
				break

				default:
				returnList.add (it-1)
				break
			}
		}
		if(max>0 && max<size){
			returnList = returnList[0..max-1]
		}
		return returnList
	}
}
