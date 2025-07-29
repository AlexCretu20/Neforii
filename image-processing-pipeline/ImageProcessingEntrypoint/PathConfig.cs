namespace ImageProcessingEntrypoint;

public static class PathConfig
{
    public static readonly string BaseDir = AppContext.BaseDirectory; // entrypoint/bin/debug/net9.0
    public static readonly string RootDir = Path.GetFullPath(Path.Combine(BaseDir, "..", "..", "..", ".."));
    public static readonly string FiltersDir = Path.GetFullPath(Path.Combine(RootDir, "Plugins", "net9.0")); // plugins
    public static readonly string InputDir = Path.Combine(RootDir, "Images", "Input");
    public static readonly string OutputDir = Path.Combine(RootDir, "Images", "Output");
    public static readonly string[] ImageExtensions = { ".jpg", ".png" };
    public static readonly string[] FilterDlls = Directory.GetFiles(FiltersDir, "*.dll");
    
}