var data = {
    url: '/api/configuration',
    applications: [],
    allApps: [],
    filter: '',
    appConfig : null,
    environments: [''],
    selEnvironment: ''
}

var app = new Vue({
    el: '#content',
    data: data,
    methods: {
        load: function(coordinate) {
            console.log("Must load", coordinate);
            axios.get(data.url + "/" + coordinate.application + "/" + coordinate.version + "/" + coordinate.environment)
                .then(function (response) {
                    console.log(response);
                    data.appConfig = response.data;
                })
                .catch(function (error) {
                    console.log(error)
                });
        },
        buildDocumentLink: function(document) {
            var coordinate = data.appConfig;
            return data.url + "/" + coordinate.application + "/" + coordinate.version + "/" + coordinate.environment + "/dynamic/" + document.key
        },
        filterApps: function() {

            var ret = Array.from(data.allApps);

            return ret.filter(function(item) {

                var isEnvironment = true;

                if (!data.selEnvironment.length == 0) {
                    isEnvironment = item.environment == data.selEnvironment;
                }

                return item.application.indexOf(data.filter) >= 0 && isEnvironment;
            });
        }
    },
    mounted: function () {
        console.log("I'm mounted")
        axios.get(data.url).then(function (response) {
            console.log(response)

            var apps = response.data;

            //reuse the sort function to populate the environments
            apps = apps.sort(function(a, b) {

                if (!data.environments.includes(a.environment)) {
                    data.environments.push(a.environment);
                }

                if (a.application < b.application && a.version < b.version && a.environment < b.environment) {
                    return -1;
                } else {
                    return 1;
                }
            })

            data.environments = data.environments.sort();

            data.allApps = Array.from(apps)
            data.applications = apps;
        }).catch(function (error) {
            console.log("Error while processing response", error);
        })
    }
})
