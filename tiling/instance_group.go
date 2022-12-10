package tiling

import "fmt"

type TemplateInstancesGroup struct {
	Template Template

	Instances []TemplateInstancesGroupInstance
}

type TemplateInstancesGroupInstance struct {
	Count    int
	FillPage bool
	Content  map[string]any
}

func (group TemplateInstancesGroup) RenderTo(renderer *DocumentRenderer) error {
	for instanceIndex, instance := range group.Instances {
		instanceSVG, err := group.Template.Render(instance.Content)
		if err != nil {
			return fmt.Errorf("prepare instance %d: %w", instanceIndex, err)
		}

		for i := 0; i < instance.Count; i++ {
			renderer.PlaceLabel(instanceSVG)
		}

		if instance.FillPage {
			if len(renderer.Pages()) == 0 {
				renderer.PlaceLabel(instanceSVG)
			}
			for renderer.IsSpaceLeftOnCurrentPage() {
				renderer.PlaceLabel(instanceSVG)
			}
		}
	}

	return nil
}
