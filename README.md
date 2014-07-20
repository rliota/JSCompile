JSCompile
=========

JSCompile is a tool for packaging disparate JavaScript files into one consolidated initialization function.
It allows you to define resource requirements inside comments with the @import annotation, enabling you to organize the
build process declarative dependencies. Additionally, it provides namespace object instantiation in the process of
pulling together all of your javascript files (to match the directories the compiler has traversed.)

It handles dependency resolution for you:
Files are wrapped in individual functions at compile time and their @import-ed files are injected as parameters
(the parameters are injected aliased to the object's file name, so you can access the objects
by their simple (non-namespaced) name in the context of the file.)

At the end of the wrapping function, the file name of the object being compiled is returned as a variable
(the file name is assumed to identify the object variable defined in the file.)

The wrapping function is then called and assigned to the file's namespace where it can be accessed from then on.

The use of closure eliminates the need to specify an object's expected namespace when defining it in a file,
allowing the file to be used as would a normal JavaScript resource. (No clunky namespace building required.)


Given a directory map for a project of
<br/>TFA
<br/>&nbsp;|---- model
<br/>&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- Evaluation.js
<br/>&nbsp;|---- view
<br/>&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- EvaluationPanel.js
<br/>&nbsp;|---- template
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- EvaluationTemplates.js

JSCompile will output an initialization function, defining namespace objects derived from the path structure of your
project like so:
<pre><code>function initialize_MyCompiledJSResources(){
var TFA = {};
var TFA.model = {};
var TFA.view = {};
var TFA.template = {};
}</code></pre>
Keeping the object instances organized in the same way they were defined in file form.

At compile time, any references to @import will be injected as parameters aliased to their simple file name:
<pre><code>//**
  * @import TFA.model.EvaluationModel
  */
EvaluationPanel = function(){
        
        . . .
        
        this.model = new EvaluationModel();
}
</code></pre>

The above code declares an @import annotation, which will instruct the compiler to first process the file found at
TFA/view/Evaluation.js, assigning its defined object "Evaluation" to the namespace TFA.model.Evaluation and then
continue to process EvaluationPanel.js' code, where TFA.model.Evaluation will be injected into the definition scope
as simply "Evaluation", rather than "TFA.model.Evaluation". The compiled code would look something like:

<pre><code>
TFA = {};
TFA.view = {};
TFA.model = {};
__define = function(){

    EvaluationModel = function(){
            . . .
    };
    return EvaluationModel;
}

TFA.model.EvaluationModel = __define();

__define = function(Evaluation){
    EvaluationPanel = function(){

            . . .

            this.model = new Evaluation();
    };
    return EvaluationPanel;
}
TFA.view.EvaluationPanel = __define(TFA.model.EvaluationModel)
</code></pre>

API
===
<code>java -jar JSCompile.jar <build directory> <output file name> </code>

Caveats
=======
Some major limitations of this compiler:
* @import annotations actually refer to the filepaths of the resource - JSCompile will not
know about the JavaScript object namespaces you create on your own.