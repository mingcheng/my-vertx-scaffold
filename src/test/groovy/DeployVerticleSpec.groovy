import com.google.common.base.Charsets
import com.google.common.io.Resources
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import spock.lang.Specification
import spock.util.concurrent.AsyncConditions

class DeployVerticleSpec extends Specification {
    Vertx vertx
    AsyncConditions conditions

    def setup() {
        vertx = Vertx.vertx()
    }

    def "Deply ServerVerticle"() {
        setup:
        conditions = new AsyncConditions(2);


        expect:
        DeploymentOptions options = new DeploymentOptions();
        URL url = Resources.getResource("application.json");
        options.setConfig(new JsonObject(Resources.toString(url, Charsets.UTF_8)));

        assert options.getConfig().containsKey("debug")
        assert options.getConfig().containsKey("services")

        vertx.deployVerticle("com.gracecode.scaffold.verticles.ServerVerticle", options) { response ->
            conditions.evaluate {
                assert response.succeeded()
            }

            vertx.setTimer(2l, {
                vertx.undeploy(response.result()) { undeployResponse ->
                    conditions.evaluate {
                        assert undeployResponse.succeeded()
                    }
                }
            })
        }

        vertx.deployVerticle("com.gracecode.scaffold.verticles.ConsumerVerticle", options) { response ->
            conditions.evaluate {
                assert response.succeeded()
            }

            vertx.setTimer(1l, {
                vertx.undeploy(response.result()) { undeployResponse ->
                    conditions.evaluate {
                        assert undeployResponse.succeeded()
                    }
                }
            })
        }

        conditions.await(5d)
    }

    def cleanup() {
        vertx.close()
    }
}
