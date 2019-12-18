package base

public class EnvironmentProperties {
    def fields = [:]


    public EnvironmentProperties(fieldsToAddToEnv){
        fieldsToAddToEnv.each {String k,String v->
            if(k != null){
                if(v != null) {
                    fields[k.trim()] = v.trim()
                }else{
                    fields[k.trim()] = v
                }
            }
        }
    }

    def getPropertyValue(String field){
        return fields[field]
    }

    def String toString(){
        def str = ""
        fields.each {k, v->
            str += "$k:  <$v> \n"
        }
        return str
    }
}
