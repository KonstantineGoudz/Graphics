import bpy


def write_some_data(context, filepath, use_some_setting):
    
    
    def veckey2d(v):
        return round(v[0], 4), round(v[1], 4)

    print("running write_some_data...")
    file = open(filepath, 'w', encoding='utf-8')
    
    w= file.write
    
    # get the selected oject
    ob = bpy.context.object
    
    # convert the oject tot a mesh applying all modifiers
    scene= bpy.data.scenes['Scene']
    me = ob.to_mesh(scene,True,'PREVIEW')
        
    verts =[]
    uvs   =[]
    faces =[]

    #search for a an entery which has the same vertex index and uv combo
    #if it does not exist add it 
    def findCopy(ind, uv):
        i=0
        for i in range(0,len(verts)):
            if (verts[i] == ind) and ( uvs[i]==uv):               
                faces.append(i)
                return i    
        verts.append(ind)
        uvs.append(uv)
        faces.append(i)    
        return i
            

    overu =0
    overf =0
    
    fw = print
    
    uv_layer = me.uv_layers.active.data
    me_verts = me.vertices[:]
    

    for p in me.polygons:
        for l in p.loop_indices:
            findCopy(me.loops[l].vertex_index,uv_layer[l].uv)

    
    w('{\"object\":{\n')
    w('\"vertices\":[\n')
    #write vertices
    for v in verts[:-1]:
        w('[%f,%f,%f],\n'%me_verts[v].co[:])
    w('[%f,%f,%f]],\n'%me_verts[-1].co[:])
    
    #write uvs
    w('\"uvs\":[\n')
    for v in uvs[:-1]:
        w('[%f,%f],\n'%v.uv.to_tuple())
    w('[%f,%f]],\n'%uvs[-1]].to_tuple())
    
    #write faces
    w('\"faces\":[\n')
    for i in range(0,len(faces)-3,3):
        w('%i,%i,%i,\n'%tuple(faces[i:i+3]))
     
    w('%i,%i,%i]}}\n'%tuple(faces[-3:]))
        
    fw('there are %d uvs' % len(uniuv))
    fw('there are %d unique vert/uv combos' % len(verts))
    fw('there are %d faces' % (len(faces)/3))
    fw('there are %d vertices' % len(me_verts))

    file.close()

    return {'FINISHED'}


# ExportHelper is a helper class, defines filename and
# invoke() function which calls the file selector.
from bpy_extras.io_utils import ExportHelper
from bpy.props import StringProperty, BoolProperty, EnumProperty
from bpy.types import Operator


class ExportSomeData(Operator, ExportHelper):
    """This appears in the tooltip of the operator and in the generated docs"""
    bl_idname = "export_test.some_data"  # important since its how bpy.ops.import_test.some_data is constructed
    bl_label = "Export to JSON"

    # ExportHelper mixin class uses this
    filename_ext = ".txt"

    filter_glob = StringProperty(
            default="*.txt",
            options={'HIDDEN'},
            )

    # List of operator properties, the attributes will be assigned
    # to the class instance from the operator settings before calling.
    use_setting = BoolProperty(
            name="Example Boolean",
            description="Example Tooltip",
            default=True,
            )

    type = EnumProperty(
            name="Example Enum",
            description="Choose between two items",
            items=(('OPT_A', "First Option", "Description one"),
                   ('OPT_B', "Second Option", "Description two")),
            default='OPT_A',
            )

    def execute(self, context):
        return write_some_data(context, self.filepath, self.use_setting)


# Only needed if you want to add into a dynamic menu
def menu_func_export(self, context):
    self.layout.operator(ExportSomeData.bl_idname, text="Export to JSON")


def register():
    bpy.utils.register_class(ExportSomeData)
    bpy.types.INFO_MT_file_export.append(menu_func_export)


def unregister():
    bpy.utils.unregister_class(ExportSomeData)
    bpy.types.INFO_MT_file_export.remove(menu_func_export)


if __name__ == "__main__":
    register()

    # test call
    bpy.ops.export_test.some_data('INVOKE_DEFAULT')

#
