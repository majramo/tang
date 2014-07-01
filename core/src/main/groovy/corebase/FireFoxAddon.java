package corebase;

public class FireFoxAddon {
	private final String directory;
	private final String name;
	private final String version;

    public FireFoxAddon(String directory, String name, String version) {
        this.directory = directory;
    	this.name = name;
        this.version = version;
    }

    public String getDirectory() {
        return directory;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
    
	public String getFullPath() {
        return String.format("%s%s-%s-fx.xpi", directory, name, version);
	}


}