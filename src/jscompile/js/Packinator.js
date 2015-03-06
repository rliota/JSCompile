
window.Packinator = window.Packinator || {

    registry: {},
    queued: [],

    getDependencies: function(dependencyNames){
        var dependenciesFound = [];
        var i=0, limit = dependencyNames.length;
        for(i; i<limit; i++){
            var dependency = this.registry[dependencyNames[i]];
            if(dependency){
                dependenciesFound.push(dependency);
            }
        }
        return dependenciesFound.length == limit ? dependenciesFound : null;
    },

    queue: function(pkg, dependencyNames){
        this.queued.push({
            pkg: pkg,
            dependencyNames: dependencyNames
        });
    },

    checkQueue: function(){
        var i=0, limit=this.queued.length;
        for(i; i<limit; i++){
            var queuedPackage = this.queued[i];
            var dependencies = this.getDependencies(queuedPackage.dependencyNames);
            if(dependencies){
                this.initializePackage(queuedPackage.pkg, dependencies);
            }
        }
    },

    initializePackage: function(pkg, dependencies){
        var i=0, limit = dependencies.length;
        for(i; i<limit; i++){
            var lib = dependencies[i];
            pkg[lib.name] = lib;
        }
        var packageName = pkg.name;
        var unpacked = pkg();
        this.registry[packageName] = unpacked;
        window[packageName] = unpacked;
    },

    "register": function(pkg, dependencyNames){
        var dependencies = dependencyNames ? this.getDependencies(dependencyNames) : [];
        if(dependencies === null){
            this.queue(pkg, dependencyNames);
        }else{
            this.initializePackage(pkg, dependencies);
            this.checkQueue();
        }
    }
};
