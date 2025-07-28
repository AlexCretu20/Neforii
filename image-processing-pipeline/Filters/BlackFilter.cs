using System.Drawing;
using AppCore;

namespace Filters;

public class BlackFilter : IFilter
{
    public Bitmap Apply(Bitmap inputImage)
    {
        var blackImg = new Bitmap(inputImage.Width, inputImage.Height);
        using (Graphics g = Graphics.FromImage(blackImg))
        {
            g.Clear(Color.Black);
        }
        return blackImg;
    }
}