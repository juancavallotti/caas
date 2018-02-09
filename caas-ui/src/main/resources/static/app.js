var data = {
    url: 'api/configuration',
    applications: [],
    appConfig : null
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
        }
    },
    mounted: function () {
        console.log("I'm mounted")
        axios.get(data.url).then(function (response) {
            console.log(response)

            var apps = response.data;

            apps.sort(function(a, b) {

                if (a.application < b.application && a.version < b.version && a.environment < b.environment) {
                    return -1;
                } else {
                    return 1;
                }
            })


            data.applications = apps;
        }).catch(function (error) {
            console.log("Error while processing response", error);
        })
    }
})
