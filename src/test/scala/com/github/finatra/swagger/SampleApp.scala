package com.github.finatra.swagger

import com.twitter.finatra.FinatraServer

object SampleApp extends FinatraServer {
  register(new SwaggerController())
  register(new SampleController)
}
