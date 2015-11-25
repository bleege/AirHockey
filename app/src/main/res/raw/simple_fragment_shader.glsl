precision mediump float;

// Uniform variable keeps value per shader call until it's explicitly changed.
uniform vec4 u_Color;

void main()
{
    gl_FragColor = u_Color;
}
