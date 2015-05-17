package fem.divider.mesh;

public interface IMeshStreamer {

	/**
	 * Saves mesh to one or more files - depending on concrete implementation
	 * of streamer.
	 * If saving to one file fileBasename becames a name of file
	 * If saving to few files those files have same name (fileBasename) and
	 * different extensions
	 * 
	 * @return
	 */
	public abstract void save(Mesh mesh, String fileBasename) throws Exception;//end save(Mesh mesh, String fileBasename)

}