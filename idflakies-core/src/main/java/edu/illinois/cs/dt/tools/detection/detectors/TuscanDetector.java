package edu.illinois.cs.dt.tools.detection.detectors;

import java.io.File;
import java.util.List;

import edu.illinois.cs.dt.tools.detection.DetectionRound;
import edu.illinois.cs.dt.tools.detection.DetectorUtil;
import edu.illinois.cs.dt.tools.detection.TestShuffler;
import edu.illinois.cs.dt.tools.detection.filters.ConfirmationFilter;
import edu.illinois.cs.dt.tools.detection.filters.UniqueFilter;
import edu.illinois.cs.dt.tools.runner.InstrumentingSmartRunner;
import edu.illinois.cs.testrunner.data.results.TestRunResult;
import edu.illinois.cs.testrunner.runner.Runner;

public class TuscanDetector extends ExecutingDetector {
    final List<String> tests;
    private TestRunResult origResult;
    private TestRunResult lastTuscanResult;
    private DetectionRound lastTuscanDetectionRound;

    private final TestShuffler testShuffler;
    public TuscanDetector(final Runner runner, final File baseDir, final int rounds, final String type, final List<String> tests) {
        super(runner, baseDir, rounds, type);
        this.tests = tests;
        this.testShuffler = new TestShuffler(type, rounds, tests, baseDir);
        this.origResult = DetectorUtil.originalResults(tests, runner);
        if (runner instanceof InstrumentingSmartRunner) {
            addFilter(new ConfirmationFilter(name, tests, (InstrumentingSmartRunner) runner));
        } else {
            addFilter(new ConfirmationFilter(name, tests, InstrumentingSmartRunner.fromRunner(runner, baseDir)));
        }

        addFilter(new UniqueFilter());
    }

    @Override
    public DetectionRound results() throws Exception {
        lastTuscanResult = runList(testShuffler.tuscanOrder(absoluteRound.get()));
        lastTuscanDetectionRound = makeDts(origResult, lastTuscanResult);
        return lastTuscanDetectionRound;
    }
    
}
