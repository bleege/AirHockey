package com.bradleege.airhockey.util;

import android.util.Log;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glShaderSource;

public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String vertextShaderSourceCode) {
        return compileShader(GL_VERTEX_SHADER, vertextShaderSourceCode);
    }

    public static int compileFragmentShader(String fragmentShaderSourceCode) {
        return compileShader(GL_FRAGMENT_SHADER, fragmentShaderSourceCode);
    }

    private static int compileShader(int type, String shaderSourceCode) {

        // Id for OpenGL Shader Object
        final int shaderObjectId = glCreateShader(type);

        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not create new shader.");
            }
            return 0;
        }

        // Load Shader Source Code Into OpenGL Shader Object referenced by shaderObjectId
        glShaderSource(shaderObjectId, shaderSourceCode);

        // Compile the Shader Source Code loaded into the OpenGL Shader object in previous step
        glCompileShader(shaderObjectId);

        // Retrieve results of Shader Compilation from Shader Object.
        // Place result in int array (compileStatus) at index specified (0 in this case)
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0;


        // TODO - Return compiled Shader
        return 0;
    }

}
