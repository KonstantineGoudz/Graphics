
import android.opengl.GLES20;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Konstantine Goudz on 11/8/2014.
 */
public class Mesh {
    private static final int COORDS_PER_VERTEX = 3;
    String name;
    FloatBuffer vertBuffer;
    FloatBuffer uvBuffer;
    ShortBuffer indices;
    int tricnt;
    

    int texture;

    public Mesh(){}

    public void loadFromJSON(String jsonData)
    {
        //
        try{

            JSONObject jsObj = new JSONObject(jsonData);
            JSONObject mesh = jsObj.getJSONObject("object");

            //get the vert array
            JSONArray vertices = mesh.getJSONArray("vertices");

            //parse vertices
            float []v=new float[vertices.length()*3];
            int index=0;
            Log.d("mesh","there are "+vertices.length()+" verts");
            for(int i=0;i<vertices.length();i++)
            {
                JSONArray vert = vertices.getJSONArray(i);
                for(int j=0;j<3;j++)
                {
                    v[index]=(float)vert.getDouble(j);
                    index++;
                }

            }
            //create the vertex buffer
            // (# of coordinate values * 4 bytes per float)
            ByteBuffer bb = ByteBuffer.allocateDirect(v.length * 4);
            bb.order(ByteOrder.nativeOrder());
            vertBuffer = bb.asFloatBuffer();
            vertBuffer.put(v);
            vertBuffer.position(0);

            JSONArray uvs = mesh.getJSONArray("uvs");
            float []uv=new float[uvs.length()*2];
            index =0;
            //parse the uv coordinates
            for(int i=0;i<uvs.length();i++)
            {
                JSONArray vert = uvs.getJSONArray(i);
                for(int j=0;j<2;j++)
                {
                   uv[index]=(float)vert.getDouble(j);
                    index++;
                }

            }

            //create the uv buffer
            // (# of coordinate values * 4 bytes per float)
            ByteBuffer bb2 = ByteBuffer.allocateDirect(uv.length * 4);
            bb2.order(ByteOrder.nativeOrder());
            uvBuffer = bb2.asFloatBuffer();
            uvBuffer.put(uv);
            uvBuffer.position(0);

            JSONArray faces = mesh.getJSONArray("faces");
            
            //array to hold the face indices 
            short []f=new short[faces.length()*3];
            
            index=0;
            tricnt = faces.length();
            for(int i=0;i<faces.length();i++)
            {
                    f[index]=(short)faces.getInt(i);
                    index++;
            }

            ByteBuffer dlb = ByteBuffer.allocateDirect(f.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            indices = dlb.asShortBuffer();
            indices.put(f);
            indices.position(0);

        }


        catch (Exception e){

        }
    }

    public void setTexture(int texture)
    {
        this.texture=texture;
    }

    public void draw(int program, float []modelMatrix, float []mvpMatrix)
    {
        GLES20.glUseProgram(program);
        int positioHhandle;
        int uvHandle;
        int texHandle;
        
        int modelMatrixdHandle;
        int mvpMatrixdHandle;

        // get atrib and uniform locations
        positioHhandle = GLES20.glGetAttribLocation(program, "vPosition");
        uvHandle = GLES20.glGetAttribLocation(program, "uvCoord");
        texHandle = GLES20.glGetAttribLocation(program, "u_Texture");

        modelMatrixdHandle = GLES20.glGetUniformLocation(program, "uMMatrix");
        mvpMatrixdHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");


        // set the texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(texHandle, 0);


        GLES20.glUniformMatrix4fv(mvpMatrixdHandle, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(modelMatrixdHandle, 1, false, modelMatrix, 0);

        // Prepare the triangle coordinate data
        GLES20.glEnableVertexAttribArray(positioHhandle);
        GLES20.glVertexAttribPointer(positioHhandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, 12, vertBuffer);

        //uv coordiante data
        GLES20.glEnableVertexAttribArray(uvHandle);
        GLES20.glVertexAttribPointer(uvHandle, 2,
                GLES20.GL_FLOAT, false, 8, uvBuffer);


        // Draw the mesh
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, tricnt,
                GLES20.GL_UNSIGNED_SHORT, indices);

        GLES20.glDisableVertexAttribArray(positioHhandle);
        GLES20.glDisableVertexAttribArray(uvHandle);

    }

}
