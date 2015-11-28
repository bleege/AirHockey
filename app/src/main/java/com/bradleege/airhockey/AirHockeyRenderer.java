package com.bradleege.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;
import com.bradleege.airhockey.util.LoggerConfig;
import com.bradleege.airhockey.util.ShaderHelper;
import com.bradleege.airhockey.util.TextResourceReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final Context context;

    // OpenGL Program
    private int program;

    // Uniform Location Data
    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    // Attribute Location Data
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    // How Many components are associated with each vertex for this attribute
    // In this case 2: x coordinate AND y coordinate
    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    public AirHockeyRenderer(Context context) {
        this.context = context;
/*
        // Original Table Rectangle
        float[] tableVertices = { 0f, 0f,
                                  0f, 14f,
                                  9f, 14f,
                                  9f, 0f };
*/

        // Table Rectangle Via Triangles
        float[] tableVerticesWithTriangles = {
                                // First Triangle
                                0f, 0f,
                                9f, 14f,
                                0f, 14f,
                                // Second Triangle
                                0f, 0f,
                                0f, 14f,
                                9f, 14f,
                                // Center Line
                                0f, 7f,
                                9f, 7f,
                                // Mallets
                                4.5f, 2f,
                                4.5f, 12f
        };

        // Creating Space In Native Memory (Not Dalvik VM Memory) for OpenGL work
        // Note the memory will be automatically freed up when the process is destroyed
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                                .order(ByteOrder.nativeOrder())
                                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    /**
     * Called to draw the current frame.
     * <p/>
     * This method is responsible for drawing the current frame.
     * <p/>
     * The implementation of this method typically looks like this:
     * <pre class="prettyprint">
     * void onDrawFrame(GL10 gl) {
     * gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
     * //... other gl calls to render the scene ...
     * }
     * </pre>
     *
     * @param gl the GL interface. Use <code>instanceof</code> to
     *           test if the interface supports GL11 or higher interfaces.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the rendering surface.
        // This will wipe out all colors on the screen and fill the screen with
        // the color previously defined by our call to glClearColor.
        glClear(GL_COLOR_BUFFER_BIT);
    }

    /**
     * Called when the surface is created or recreated.
     * <p/>
     * Called when the rendering thread
     * starts and whenever the EGL context is lost. The EGL context will typically
     * be lost when the Android device awakes after going to sleep.
     * <p/>
     * Since this method is called at the beginning of rendering, as well as
     * every time the EGL context is lost, this method is a convenient place to put
     * code to create resources that need to be created when the rendering
     * starts, and that need to be recreated when the EGL context is lost.
     * Textures are an example of a resource that you might want to create
     * here.
     * <p/>
     * Note that when the EGL context is lost, all OpenGL resources associated
     * with that context will be automatically deleted. You do not need to call
     * the corresponding "glDelete" methods such as glDeleteTextures to
     * manually delete these lost resources.
     * <p/>
     *
     * @param gl     the GL interface. Use <code>instanceof</code> to
     *               test if the interface supports GL11 or higher interfaces.
     * @param config the EGLConfig of the created surface. Can be used
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Clears Screen
        // Specifically, clears screen to display Red when done
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // Load In OpenGL Shaders
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        // Compile the shaders
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        // Get OpenGL program
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        // Validate OpenGL Program
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        // Set OpenGL program to be used for any drawing
        glUseProgram(program);

        // Get Uniform Location
        uColorLocation = glGetUniformLocation(program, U_COLOR);

        // Get Attribute Location Once Shaders have been bound
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        // Tell OpenGL where to find data for our attribute a_Position
        // ===========================================================
        // Ensure OpenGL starts at beginning of data buffer
        vertexData.position(0);

        // Tell OpenGL that it can find data for a_Position in vertexData
        // glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, Buffer ptr)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);

        // Enable Vertex Data Array so OpenGL can find all data
        glEnableVertexAttribArray(aPositionLocation);
    }

    /**
     * Called when the surface changed size.
     * <p/>
     * Called after the surface is created and whenever
     * the OpenGL ES surface size changes.
     * <p/>
     * Typically you will set your viewport here. If your camera
     * is fixed then you could also set your projection matrix here:
     * <pre class="prettyprint">
     * void onSurfaceChanged(GL10 gl, int width, int height) {
     * gl.glViewport(0, 0, width, height);
     * // for a fixed camera, set the projection too
     * float ratio = (float) width / height;
     * gl.glMatrixMode(GL10.GL_PROJECTION);
     * gl.glLoadIdentity();
     * gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
     * }
     * </pre>
     *
     * @param gl     the GL interface. Use <code>instanceof</code> to
     *               test if the interface supports GL11 or higher interfaces.
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface of
        // the regularly specified view used to display OpenGL
        glViewport(0, 0, width, height);
    }
}
