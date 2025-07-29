using System.Drawing;
using AppCore;

namespace DoNothingFilter;

public class DoNothingFilter : IFilter
{ 
    public Bitmap Apply(Bitmap inputImage)
    {
        return inputImage;
    }
}