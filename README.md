JSCompile
=========
The smartest tool for packaging a JS project.

Sick of writing RequireJS boiler plate just to get your project compiled? JSCompile has you covered.
Given a directory map for a project that looks like:
<br/>TFA
<br/>&nbsp;|---- model
<br/>&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- EvaluationModel.js
<br/>&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- PersonModel.js
<br/>&nbsp;|---- view
<br/>&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- EvaluationPanel.js
<br/>&nbsp;|---- template
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|---- EvaluationTemplates.js

and a file (EvaluationPanel.js for instance) that looks like:

    /**
        @import TFA.models.EvaluationModel;
        @import TFA.models.PersonModel;
     */
    function EvaluationPanel(personData){
        this.evaluationData = new EvaluationModel();
        this.personData = personData || new PersonModel();
    }


Running JSCompile will output an initialization function, defining namespace objects derived from the path structure of your
project and then inject them for you to use shorthand:

    function initialize_MyCompiledJSResources(){
        var TFA = {};
        TFA.model = {};
        TFA.view = {};
        TFA.template = {};

        TFA.model.PersonModel = (function(){
            . . .
        }());

        TFA.model.EvaluationModel = (function(){
            . . .
        }());

        TFA.view.EvaluationPanel = (function(EvaluationModel, PersonModel){

            function EvaluationPanel(personData){
                this.evaluationData = new EvaluationModel();
                this.personData = personData || new PersonModel();
            }
        }(TFA.model.EvaluationModel, TFA.model.PersonModel);

        . . .

        return TFA;
    }



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




The above code declares an @import annotation, which will instruct the compiler to first process the file found at
TFA/model/EvaluationModel.js, assigning its defined object "EvaluationModel" to the namespace TFA.model.EvaluationModel and then continue to process EvaluationPanel.js' code, where TFA.model.EvaluationModel will be injected into the definition scope as simply "EvaluationModel", rather than the more verbose "TFA.model.EvaluationModel". The compiled code would look something like:

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

__define = function(EvaluationModel){
    EvaluationPanel = function(){

            . . .

            this.model = new EvaluationModel();
    };
    return EvaluationPanel;
}
TFA.view.EvaluationPanel = __define(TFA.model.EvaluationModel)
</code></pre>

Making the process of using components defined in other files not only possible, but also painless (compared to version 0 where the namespaces had to be referenced.)

API
===
<code>java -jar JSCompile.jar &lt;build directory&gt; &lt;output file name&gt; </code>

Caveats
=======
Some major limitations of this compiler:
* @import annotations actually refer to the filepaths of the resource - JSCompile will not
know about the JavaScript object namespaces you create on your own.
