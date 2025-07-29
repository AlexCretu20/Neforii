using System.Drawing;
using AppCore;

namespace Filters.GrayscaleFilter;

public class GrayscaleFilter : IFilter
{
    public Bitmap Apply(Bitmap inputImage)
    {
        var grayImg = new Bitmap(inputImage.Width, inputImage.Height);
        for (int y = 0; y < inputImage.Height; y++)
        {
            for (int x = 0; x < inputImage.Width; x++)
            {
                var pixel = inputImage.GetPixel(x, y);
                var grayValue = (int)(pixel.R * 0.3 + pixel.G * 0.59 + pixel.B * 0.11); // formula de pe net pentru greyscale, R G B nu sunt toate egale ca importanta
                var grayColor = Color.FromArgb(grayValue, grayValue, grayValue);
                grayImg.SetPixel(x, y, grayColor);
            }
        }
        return grayImg;
    }
}