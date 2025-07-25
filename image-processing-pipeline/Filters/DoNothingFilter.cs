using System.Drawing;
using AppCore;

public class DoNothingFilter : IFilter
{ 
    public Bitmap Apply(Bitmap inputImage)
    {
        return inputImage;
    }
}
