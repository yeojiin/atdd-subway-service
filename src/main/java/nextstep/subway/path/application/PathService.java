package nextstep.subway.path.application;

import nextstep.subway.exception.StationNotFoundException;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.dto.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.vo.Path;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.utils.Message.NOT_EXISTS_STATION;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }


    public PathResponse findShortestPath(Long source, Long target) {
        Station sourceStation = findStationById(source);
        Station targetStation = findStationById(target);
        List<Section> sections = findAllSections();

        PathFinder pathFinder = PathFinder.from(sections);
        Path path = pathFinder.findAllStationsByStations(sourceStation, targetStation);

        return PathResponse.of(path.getStations(), path.getDistance());
    }


    private List<Section> findAllSections() {
        return lineRepository.findAll()
                .stream()
                .flatMap(l -> l.getSections().stream())
                .collect(Collectors.toList());
    }

    private Station findStationById(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationNotFoundException(NOT_EXISTS_STATION));
    }
}
