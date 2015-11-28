// Attribute variable needs to have it's value set per each shader call
attribute vec4 a_Position;

void main()
{
    gl_Position = a_Position;
}
